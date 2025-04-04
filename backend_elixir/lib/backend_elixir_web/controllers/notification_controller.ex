defmodule BackendElixirWeb.NotificationController do
  use BackendElixirWeb, :controller

  def index(conn, _params) do
    # Example response
    json(conn, %{message: "Notifications endpoint is working!"})
  end
end
