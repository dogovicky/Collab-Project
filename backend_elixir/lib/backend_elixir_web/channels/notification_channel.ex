defmodule BackendElixirWeb.NotificationChannel do
  use Phoenix.Channel

  @impl true
  def join("notifications:" <> user_id, _params, socket) do
    {:ok, assign(socket, :user_id, user_id)}
  end

  # Channels can be used in a request/response fashion
  # handle incoming notification
  @impl true
  def handle_in("new_notification", %{"message" => message, "type" => type}, socket) do
    notification = %{
      user_id: socket.assigns.user_id,
      message: message,
      type: type,
      timestamp: DateTime.utc_now()
    }

    broadcast(socket, "new_notification", notification)
    # acknowledge receipt
    {:reply, :ok, socket}
  end

  # Handle "fetch_messages" event
  @impl true
  def handle_in("fetch_messages", _payload, socket) do
    # Replace this with your actual logic to fetch messages
    messages = [
      %{id: 1, content: "Hello from Phoenix!"},
      %{id: 2, content: "Another message"},
      %{id: 3, content: "Chapo nne na dengu"}
    ]

    {:reply, {:ok, %{messages: messages}}, socket}
  end
end
