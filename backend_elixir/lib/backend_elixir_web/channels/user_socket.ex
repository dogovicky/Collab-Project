defmodule BackendElixirWeb.UserSocket do
  use Phoenix.Socket

  # Require Logger to use its macros
  require Logger

  ## Channels
  channel "notifications:*", BackendElixirWeb.NotificationChannel

  # Handle WebSocket connection
  @impl true
  def connect(params, socket, _connect_info) do
    Logger.info("WebSocket connection attempt with params: #{inspect(params)}")

    user_id = Map.get(params, "user_id", "guest")
    {:ok, assign(socket, :user_id, user_id)}
  end

  # Generate a unique socket ID for each user
  @impl true
  def id(socket) do
    Logger.info("Generated socket ID: user_socket:#{socket.assigns.user_id}")
    "user_socket:#{socket.assigns.user_id}"
  end
end
