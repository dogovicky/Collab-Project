defmodule BackendElixirWeb.NotificationChannel do
  use Phoenix.Channel

  @impl true
  def join("notification:" <> user_id, _params, socket) do
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
end
