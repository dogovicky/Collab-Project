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
      heartbeat: 60,
      connection_timeout: 10_000
    ]

    case AMQP.Connection.open(rabbitmq_url, connection_options) do
      {:ok, conn} ->
        case AMQP.Channel.open(conn) do
          {:ok, chan} ->
            setup_rabbitmq(chan)
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

  defp setup_rabbitmq(chan) do
    AMQP.Exchange.declare(chan, @exchange, :direct, durable: true)
    AMQP.Queue.declare(chan, @queue, durable: true, auto_delete: false)
    AMQP.Queue.bind(chan, @queue, @exchange, routing_key: @routing_key)
    {:ok, _consumer_tag} = AMQP.Basic.consume(chan, @queue, nil, no_ack: false)
    Logger.info("[RabbitMQ] Listening for messages on queue: #{@queue}...")
  end

  # Handle incoming messages from RabbitMQ
  def handle_info({:basic_deliver, payload, meta}, state) do
    Logger.info("Received message: #{payload}")

    case Jason.decode(payload) do
      {:ok,
       %{"firstName" => _first_name, "lastName" => _last_name, "username" => _username} = payload} ->
        handle_valid_payload(payload, state, meta)

      {:ok, %{"user_id" => _user_id, "message" => _message} = payload} ->
        handle_minimal_payload(payload, state, meta)

      {:ok, unexpected_payload} ->
        Logger.error("Unexpected payload structure: #{inspect(unexpected_payload)}")
        reject_message(state.channel, meta.delivery_tag, requeue: false)

      {:error, _reason} ->
        Logger.error("Failed to decode JSON message: #{payload}")
        reject_message(state.channel, meta.delivery_tag, requeue: false)
    end

    {:noreply, state}
  end

  # Handle RabbitMQ system messages
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

  # Catch-all clause for unknown messages
  def handle_info(unknown_message, state) do
    Logger.warning("Unhandled message: #{inspect(unknown_message)}")
    {:noreply, state}
  end

  # Handle full payloads
  defp handle_valid_payload(payload, state, meta) do
    %{
      "firstName" => first_name,
      "lastName" => last_name,
      "username" => username,
      "email" => email,
      "dateOfBirth" => date_of_birth,
      "gender" => gender,
      "fieldOfInterest" => field_of_interest,
      "institution" => institution
    } = payload

    Logger.info("""
    Processing full payload:
    First Name: #{first_name}
    Last Name: #{last_name}
    Username: #{username}
    Email: #{email}
    Date of Birth: #{inspect(date_of_birth)}
    Gender: #{gender}
    Field of Interest: #{inspect(field_of_interest)}
    Institution: #{institution}
    """)

    # Broadcast the notification to the frontend via WebSocket
    Endpoint.broadcast!("notifications:#{username}", "new_notification", %{
      user_id: username,
      message: "Hello #{first_name} #{last_name}, welcome to #{institution}!"
    })

    Logger.info("Sent notification to WebSocket for user #{username}")

    # Acknowledge the message
    AMQP.Basic.ack(state.channel, meta.delivery_tag)
  end

  # Handle minimal payloads
  defp handle_minimal_payload(payload, state, meta) do
    %{"user_id" => user_id, "message" => message} = payload

    Logger.info("""
    Processing minimal payload:
    User ID: #{user_id}
    Message: #{message}
    """)

    # Broadcast the notification to the frontend via WebSocket
    Endpoint.broadcast!("notifications:#{user_id}", "new_notification", %{
      user_id: user_id,
      message: message
    })

    Logger.info("Sent notification to WebSocket for user #{user_id}")

    # Acknowledge the message
    AMQP.Basic.ack(state.channel, meta.delivery_tag)
  end

  # Reject invalid messages
  defp reject_message(channel, delivery_tag, requeue) do
    AMQP.Basic.reject(channel, delivery_tag, requeue: requeue)
  end

  # Ensure RabbitMQ connection is closed on termination
  def terminate(reason, %{connection: conn, channel: chan}) do
    Logger.info("Terminating RabbitMQ connection: #{inspect(reason)}")

    try do
      AMQP.Channel.close(chan)
      AMQP.Connection.close(conn)
    catch
      _, _ -> :ok
    end
  end

  # SSL options
  defp load_ssl_options(rabbitmq_host) do
    system_cert_path = "/home/elon/cacert.pem"

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

      [verify: :verify_none, versions: [:"tlsv1.2", :"tlsv1.3"]]
    end
  end
end
