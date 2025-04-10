defmodule BackendElixir.RabbitMQ do
  use GenServer
  require Logger
  alias AMQP.{Channel, Connection, Queue, Basic}

  @rabbitmq_url System.get_env("RABBITMQ_URL") ||
                  raise(
                    "ERROR: RABBITMQ_URL is not set. Please export it before running the app."
                  )

  @rabbitmq_host URI.parse(@rabbitmq_url).host ||
                   raise("ERROR: Failed to extract RabbitMQ host from RABBITMQ_URL")

  @cacertfile "/home/vicky/Java Web/Collab-Project/backend_elixir/cacert.pem"

  # Starts the GenServer process
  def start_link(_) do
    GenServer.start_link(__MODULE__, nil, name: __MODULE__)
  end

  # Initializes RabbitMQ connection and channel
  def init(_) do
    case connect() do
      {:ok, conn, chan} -> {:ok, %{connection: conn, channel: chan}}
      {:error, reason} -> {:stop, reason}
    end
  end

  # Public function to get the channel
  def get_channel do
    GenServer.call(__MODULE__, :get_channel)
  end

  # Connects to RabbitMQ with robust SSL options
  defp connect(retries \\ 5) do
    if retries <= 0 do
      Logger.error("Exceeded maximum retries for RabbitMQ connection.")
      {:error, :max_retries_exceeded}
    else
      ssl_options = load_ssl_options()

      Logger.info("Connecting to RabbitMQ at #{@rabbitmq_url} with SSL configuration")

      connection_options = [
        ssl_options: ssl_options,
        # Add heartbeat to maintain connection
        heartbeat: 30,
        # Increased timeout
        connection_timeout: 10_000
      ]

      case Connection.open(@rabbitmq_url, connection_options) do
        {:ok, conn} ->
          Logger.info("Successfully connected to RabbitMQ")

          case Channel.open(conn) do
            {:ok, chan} ->
              Logger.info("Successfully opened RabbitMQ channel")
              {:ok, conn, chan}

            {:error, reason} ->
              Logger.error("Failed to open RabbitMQ channel: #{inspect(reason)}")
              Connection.close(conn)
              {:error, reason}
          end

        {:error, reason} ->
          Logger.error("RabbitMQ connection error: #{inspect(reason)}. Retrying in 5s...")
          Process.sleep(5000)
          connect(retries - 1)
      end
    end
  end

  # Robust SSL options loading
  defp load_ssl_options do
    host = @rabbitmq_host

    [
      verify: :verify_peer,
      fail_if_no_peer_cert: true,
      depth: 5,
      cacertfile: @cacertfile,
      server_name_indication: to_charlist(host),
      customize_hostname_check: [
        match_fun: :public_key.pkix_verify_hostname_match_fun(:https)
      ],
      # Specify TLS versions
      versions: [:"tlsv1.2", :"tlsv1.3"]
    ]
  rescue
    _ ->
      Logger.warning("Falling back to non-verified SSL connection")
      [verify: :verify_none]
  end

  # Handles the get_channel GenServer call
  def handle_call(:get_channel, _from, %{channel: channel} = state) do
    {:reply, channel, state}
  end

  # Handles message publishing
  def handle_call({:publish, queue, message}, _from, %{channel: channel} = state) do
    case Basic.publish(channel, "", queue, message) do
      :ok ->
        Logger.info("Message published to queue: #{queue}")
        {:reply, :ok, state}

      {:error, reason} ->
        Logger.error("Failed to publish message: #{inspect(reason)}")
        {:reply, {:error, reason}, state}
    end
  end

  # Publishes a message to a queue
  def publish(queue, message) do
    GenServer.call(__MODULE__, {:publish, queue, message})
  end

  # Consumes messages from a queue
  def consume(queue) do
    GenServer.cast(__MODULE__, {:consume, queue})
  end

  # Handles message consumption
  def handle_cast({:consume, queue}, %{channel: channel} = state) do
    # Declare queue safely
    case Queue.declare(channel, queue) do
      {:ok, _} -> Logger.info("Queue #{queue} declared successfully")
      {:error, reason} -> Logger.error("Failed to declare queue #{queue}: #{inspect(reason)}")
    end

    # Start consuming messages
    case Basic.consume(channel, queue) do
      {:ok, _consumer_tag} ->
        {:noreply, state}

      {:error, reason} ->
        Logger.error("Failed to consume messages: #{inspect(reason)}")
        {:noreply, state}
    end
  end

  # Handle connection termination
  def terminate(reason, %{connection: conn, channel: chan}) do
    Logger.info("Terminating RabbitMQ connection: #{inspect(reason)}")
    Channel.close(chan)
    Connection.close(conn)
  end

  # Handles successful consumer registration
  def handle_info({:basic_consume_ok, %{consumer_tag: consumer_tag}}, state) do
    Logger.info("Consumer registered with tag: #{consumer_tag}")
    {:noreply, state}
  end

  # Handles received messages
  def handle_info({:basic_deliver, payload, meta}, %{channel: channel} = state) do
    Logger.info("Received message: #{inspect(payload)}")
    # Acknowledge the message
    Basic.ack(channel, meta.delivery_tag)
    {:noreply, state}
  end

  # Handles unexpected messages
  def handle_info(msg, state) do
    Logger.warning("Received unexpected message: #{inspect(msg)}")
    {:noreply, state}
  end
end
