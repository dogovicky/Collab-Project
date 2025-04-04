defmodule BackendElixirWeb.Router do
  use BackendElixirWeb, :router

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/api", BackendElixirWeb do
    pipe_through :api

    # Define API routes here
    get "/notifications", NotificationController, :index
  end

  # Enable LiveDashboard and Swoosh mailbox preview in development
  if Application.compile_env(:backend_elixir, :dev_routes) do
    import Phoenix.LiveDashboard.Router

    scope "/dev" do
      pipe_through [:fetch_session, :protect_from_forgery]

      live_dashboard "/dashboard", metrics: BackendElixirWeb.Telemetry
      forward "/mailbox", Plug.Swoosh.MailboxPreview
    end
  end
end
