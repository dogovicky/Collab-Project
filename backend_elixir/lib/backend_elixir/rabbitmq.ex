defmodule BackendElixir.RabbitMQ do
  use GenServer
  require Logger
  alias AMQP.{Channel, Queue, Basic}

  # Fetch RabbitMQ URL and SSL options at runtime
  @rabbitmq_url Application.compile_env(
                  :backend_elixir,
                  :rabbitmq_url,
                  "amqps://your-default-url"
                )
  @ssl_options Application.compile_env(:backend_elixir, :rabbitmq_ssl_options, [])

  # Starts the GenServer process and registers it under the module name.
  def start_link(_) do
    GenServer.start_link(__MODULE__, nil, name: __MODULE__)
  end

  # Initializes the GenServer by establishing a connection and channel.
  def init(_) do
    case connect() do
      {:ok, conn, chan} ->
        {:ok, %{connection: conn, channel: chan}}

      {:error, reason} ->
        Logger.error("Failed to connect to RabbitMQ: #{inspect(reason)}")
        {:stop, reason}
    end
  end

  # Establishes a connection to RabbitMQ and opens a channel with retries.
  defp connect do
    Logger.info(
      "Attempting to connect to RabbitMQ at #{@rabbitmq_url} with SSL options: #{@ssl_options}"
    )

    case AMQP.Connection.open(@rabbitmq_url, ssl_options: @ssl_options) do
      {:ok, conn} ->
        Logger.info("Successfully connected to RabbitMQ")

        case Channel.open(conn) do
          {:ok, chan} ->
            Logger.info("Successfully opened RabbitMQ channel")
            {:ok, conn, chan}

          {:error, reason} ->
            Logger.error("Failed to open RabbitMQ channel: #{inspect(reason)}")
            {:error, reason}
        end

      {:error, reason} ->
        Logger.error("RabbitMQ connection error: #{inspect(reason)}. Retrying in 5s...")
        Process.sleep(5000)
        connect()
    end
  end

  # Publishes a message to the specified queue.
  def publish(queue, message) do
    GenServer.call(__MODULE__, {:publish, queue, message})
  end

  # Initiates consumption of messages from the specified queue.
  def consume(queue) do
    GenServer.cast(__MODULE__, {:consume, queue})
  end

  # Handles the GenServer call to publish a message.
  def handle_call({:publish, queue, message}, _from, state) do
    case Basic.publish(state.channel, "", queue, message) do
      :ok ->
        {:reply, :ok, state}

      {:error, reason} ->
        Logger.error("Failed to publish message: #{inspect(reason)}")
        {:reply, {:error, reason}, state}
    end
  end

  # Handles the GenServer cast to consume messages.
  def handle_cast({:consume, queue}, state) do
    case Queue.subscribe(state.channel, queue, fn payload, _meta ->
           Logger.info("Received message: #{inspect(payload)}")
         end) do
      {:ok, _consumer_tag} ->
        {:noreply, state}

      {:error, reason} ->
        Logger.error("Failed to consume messages: #{inspect(reason)}")
        {:noreply, state}
    end
  end
end
