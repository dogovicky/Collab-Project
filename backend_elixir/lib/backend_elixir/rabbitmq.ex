defmodule BackendElixir.RabbitMQ do
  use GenServer
  require Logger
  alias AMQP.{Connection, Channel, Queue, Basic}

  # Start GenServer
  def start_link(_) do
    GenServer.start_link(__MODULE__, nil, name: __MODULE__)
  end

  # Initialize connection
  def init(_) do
    case connect() do
      {:ok, conn} ->
        {:ok, %{connection: conn, channel: setup_channel(conn)}}

      {:error, reason} ->
        Logger.error("Failed to connect to RabbitMQ: #{inspect(reason)}")
        {:stop, reason}
    end
  end

  # Connect to RabbitMQ
  defp connect do
    rabbitmq_url = Application.get_env(:backend_elixir, BackendElixir.RabbitMQ)[:amqp_url]

    if is_nil(rabbitmq_url) or rabbitmq_url == "" do
      Logger.error("RABBITMQ_URL is not set properly.")
      {:error, :missing_rabbitmq_url}
    else
      Connection.open(rabbitmq_url)
    end
  end

  # Set up a channel
  defp setup_channel(conn) do
    case Channel.open(conn) do
      {:ok, chan} ->
        Logger.info("RabbitMQ channel opened successfully!")
        chan

      {:error, reason} ->
        Logger.error("Failed to open RabbitMQ channel: #{inspect(reason)}")
        nil
    end
  end

  #  Publish a message to a queue
  def publish(queue, message) do
    case connect() do
      {:ok, conn} ->
        {:ok, chan} = Channel.open(conn)
        Queue.declare(chan, queue, durable: true)
        Basic.publish(chan, "", queue, message)
        Logger.info("Published message: #{message} to queue: #{queue}")
        :ok

      {:error, reason} ->
        Logger.error("Failed to publish message: #{inspect(reason)}")
        {:error, reason}
    end
  end

  #  Consume messages from a queue
  def consume(queue) do
    case connect() do
      {:ok, conn} ->
        {:ok, chan} = Channel.open(conn)
        Queue.declare(chan, queue, durable: true)

        Logger.info("Waiting for messages in queue: #{queue}...")

        # Set up a consumer that listens indefinitely
        {:ok, _consumer_tag} = Basic.consume(chan, queue, nil, no_ack: true)

        # This will keep the process alive and wait for messages
        receive_messages()

      {:error, reason} ->
        Logger.error("Failed to consume message: #{inspect(reason)}")
    end
  end

  # Helper function to process messages
  defp receive_messages do
    receive do
      {:basic_deliver, payload, _meta} ->
        Logger.info("Received message: #{payload}")
        # Keep listening for new messages
        receive_messages()
    end
  end
end
