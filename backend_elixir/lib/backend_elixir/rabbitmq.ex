defmodule BackendElixir.RabbitMQ do
  use GenServer
  require Logger
  alias AMQP.{Channel, Connection, Queue, Basic}

  # Fetch RabbitMQ URL and SSL options at runtime
  @rabbitmq_url System.get_env(
                  "RABBITMQ_URL",
                  "amqps://slzjfjxx:UsrrZc_Z1dWw2zz03GwpSGrRPDtrlzhX@cow.rmq2.cloudamqp.com:5671/slzjfjxx"
                )

  # SSL Options (Ensure CA Cert file is correctly loaded)
  @cacertfile "/home/elon/cacert.pem"

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
    ssl_options =
      if File.exists?(@cacertfile) do
        [
          verify: :verify_peer,
          cacertfile: @cacertfile,
          server_name_indication: ~c"cow.rmq2.cloudamqp.com",
          customize_hostname_check: [
            match_fun: :public_key.pkix_verify_hostname_match_fun(:https)
          ]
        ]
      else
        Logger.warning("CA Cert file not found. Skipping SSL verification.")
        []
      end

    Logger.info(
      "Attempting to connect to RabbitMQ at #{@rabbitmq_url} with SSL options: #{inspect(ssl_options)}"
    )

    case Connection.open(@rabbitmq_url, ssl_options: ssl_options) do
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
  def handle_call({:publish, queue, message}, _from, %{channel: channel} = state) do
    case Basic.publish(channel, "", queue, message) do
      :ok ->
        {:reply, :ok, state}

      {:error, reason} ->
        Logger.error("Failed to publish message: #{inspect(reason)}")
        {:reply, {:error, reason}, state}
    end
  end

  # Handles the GenServer cast to consume messages.
  def handle_cast({:consume, queue}, %{channel: channel} = state) do
    # Declare the queue if it does not exist
    case Queue.declare(channel, queue) do
      {:ok, _} ->
        Logger.info("Queue #{queue} declared successfully")

      {:error, reason} ->
        Logger.error("Failed to declare queue #{queue}: #{inspect(reason)}")
        {:noreply, state}
    end

    case Basic.consume(channel, queue) do
      {:ok, _consumer_tag} ->
        {:noreply, state}

      {:error, reason} ->
        Logger.error("Failed to consume messages: #{inspect(reason)}")
        {:noreply, state}
    end
  end

  # Handles unexpected messages
  def handle_info({:basic_consume_ok, %{consumer_tag: consumer_tag}}, state) do
    Logger.info("Consumer registered with tag: #{consumer_tag}")
    {:noreply, state}
  end

  def handle_info({:basic_deliver, payload, meta}, %{channel: channel} = state) do
    Logger.info("Received message: #{inspect(payload)}")
    # Acknowledge the message
    Basic.ack(channel, meta.delivery_tag)
    {:noreply, state}
  end

  def handle_info(msg, state) do
    Logger.error("Received unexpected message: #{inspect(msg)}")
    {:noreply, state}
  end
end
