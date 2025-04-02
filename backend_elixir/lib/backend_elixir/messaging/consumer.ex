defmodule BackendElixir.Messaging.Consumer do
  use GenServer
  require Logger
  alias BackendElixirWeb.Endpoint

  @queue "notifications.queue"
  @exchange "notifications.exchange"
  @routing_key "notifications.key"

  # Start the Consumer when the app starts
  def start_link(opts \\ []) do
    GenServer.start_link(__MODULE__, opts, name: __MODULE__)
  end

  # Setup Connection to RabbitMQ
  def init(opts) do
    # Allow URL to be passed as an option, otherwise use environment variable
    rabbitmq_url = Keyword.get(opts, :url) || System.get_env("RABBITMQ_URL")
    rabbitmq_host = Keyword.get(opts, :host) || System.get_env("RABBITMQ_HOST")

    cond do
      is_nil(rabbitmq_url) ->
        Logger.error("RABBITMQ_URL is not set! Check your environment variables.")
        {:stop, :missing_rabbitmq_url}

      is_nil(rabbitmq_host) ->
        Logger.error("RABBITMQ_HOST is not set! Check your environment variables.")
        {:stop, :missing_rabbitmq_host}

      true ->
        connect_to_rabbitmq(rabbitmq_url, rabbitmq_host)
    end
  end

  # Centralized connection logic
  defp connect_to_rabbitmq(rabbitmq_url, rabbitmq_host) do
    Logger.info("Connecting to RabbitMQ at #{rabbitmq_url}...")

    ssl_options = load_ssl_options(rabbitmq_host)

    connection_options = [
      ssl_options: ssl_options,
      # Increased heartbeat interval
      heartbeat: 60,
      connection_timeout: 10_000
    ]

    case AMQP.Connection.open(rabbitmq_url, connection_options) do
      {:ok, conn} ->
        case AMQP.Channel.open(conn) do
          {:ok, chan} ->
            # Declare exchange
            case AMQP.Exchange.declare(chan, @exchange, :direct, durable: true) do
              :ok ->
                Logger.info("RabbitMQ exchange '#{@exchange}' declared successfully.")

              {:error, reason} ->
                Logger.error("Failed to declare RabbitMQ exchange: #{inspect(reason)}")
                {:stop, reason}
            end

            # Declare queue with additional options
            case AMQP.Queue.declare(chan, @queue, durable: true, auto_delete: false) do
              {:ok, _} ->
                Logger.info("RabbitMQ queue '#{@queue}' declared successfully.")

              {:error, reason} ->
                Logger.error("Failed to declare RabbitMQ queue: #{inspect(reason)}")
                {:stop, reason}
            end

            # Bind queue to exchange with routing key
            case AMQP.Queue.bind(chan, @queue, @exchange, routing_key: @routing_key) do
              :ok ->
                Logger.info(
                  "RabbitMQ queue '#{@queue}' bound to exchange '#{@exchange}' with routing key '#{@routing_key}'."
                )

              {:error, reason} ->
                Logger.error("Failed to bind queue: #{inspect(reason)}")
                {:stop, reason}
            end

            # Setup consumer with manual acknowledgement
            {:ok, _consumer_tag} = AMQP.Basic.consume(chan, @queue, nil, no_ack: false)
            Logger.info("[RabbitMQ] Listening for messages on queue: #{@queue}...")

            {:ok, %{channel: chan, connection: conn}}

          {:error, reason} ->
            Logger.error("Failed to open RabbitMQ channel: #{inspect(reason)}")
            {:stop, reason}
        end

      {:error, reason} ->
        Logger.error("Failed to connect to RabbitMQ: #{inspect(reason)}")
        {:stop, reason}
    end
  end

  # Robust SSL options loading
  defp load_ssl_options(rabbitmq_host) do
    # Use system CA certificates
    system_cert_path = "/home/elon/cacert.pem"

    # Fallback options if system certificates are not available
    fallback_options = [
      verify: :verify_none,
      versions: [:"tlsv1.2", :"tlsv1.3"]
    ]

    try do
      if File.exists?(system_cert_path) do
        [
          verify: :verify_peer,
          fail_if_no_peer_cert: false,
          depth: 5,
          cacertfile: system_cert_path,
          server_name_indication: to_charlist(rabbitmq_host),
          customize_hostname_check: [
            match_fun: :public_key.pkix_verify_hostname_match_fun(:https)
          ],
          versions: [:"tlsv1.2", :"tlsv1.3"]
        ]
      else
        Logger.warning(
          "System CA certificates not found. Falling back to non-verified SSL connection."
        )

        fallback_options
      end
    rescue
      _ ->
        Logger.warning(
          "Error loading SSL certificates. Falling back to non-verified SSL connection."
        )

        fallback_options
    end
  end

  # Handle system messages from RabbitMQ
  def handle_info({:basic_consume_ok, _info}, state) do
    Logger.info("Successfully subscribed to RabbitMQ queue.")
    {:noreply, state}
  end

  def handle_info({:basic_cancel, _info}, state) do
    Logger.warning("RabbitMQ subscription was canceled.")
    {:noreply, state}
  end

  def handle_info({:basic_cancel_ok, _info}, state) do
    Logger.warning("RabbitMQ consumer canceled successfully.")
    {:noreply, state}
  end

  # Handle Incoming Messages from RabbitMQ

  def handle_info({:basic_deliver, payload, meta}, state) do
    Logger.info("Received message: #{payload}")

    # Decode the JSON payload
    case Jason.decode(payload) do
      {:ok, %{"username" => user_id, "fullName" => message}} ->
        # Broadcast the notification to the frontend via WebSocket
        Endpoint.broadcast!("notifications:#{user_id}", "new_notification", %{
          user_id: user_id,
          message: message
        })

        Logger.info("Sent notification to WebSocket for user #{user_id}: #{message}")

        # Acknowledge the message
        AMQP.Basic.ack(state.channel, meta.delivery_tag)

      {:ok, decoded_payload} ->
        Logger.error("Unexpected payload structure: #{inspect(decoded_payload)}")

        # Reject the message and do not requeue
        AMQP.Basic.reject(state.channel, meta.delivery_tag, requeue: false)

      {:error, _reason} ->
        Logger.error("Failed to decode JSON message: #{payload}")

        # Reject the message and do not requeue
        AMQP.Basic.reject(state.channel, meta.delivery_tag, requeue: false)
    end

    {:noreply, state}
  end

  # Catch-all clause for unknown messages
  def handle_info(unknown_message, state) do
    Logger.warning("Unhandled message: #{inspect(unknown_message)}")
    {:noreply, state}
  end

  # Terminate callback to properly close connection
  def terminate(reason, %{connection: conn, channel: chan}) do
    Logger.info("Terminating RabbitMQ connection: #{inspect(reason)}")

    try do
      AMQP.Channel.close(chan)
      AMQP.Connection.close(conn)
    catch
      _, _ -> :ok
    end
  end
end
