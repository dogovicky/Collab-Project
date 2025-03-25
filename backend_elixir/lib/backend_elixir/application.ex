defmodule BackendElixir.Application do
  @moduledoc false

  use Application

  @impl true
  def start(_type, _args) do
    children = [
      BackendElixirWeb.Telemetry,
      BackendElixir.Repo,
      BackendElixir.RabbitMQ,
      {DNSCluster, query: Application.get_env(:backend_elixir, :dns_cluster_query) || :ignore},
      {Phoenix.PubSub, name: BackendElixir.PubSub},
      {Finch, name: BackendElixir.Finch},
      BackendElixirWeb.Endpoint
    ]

    opts = [strategy: :one_for_one, name: BackendElixir.Supervisor]
    Supervisor.start_link(children, opts)
  end

  @impl true
  def config_change(changed, _new, removed) do
    BackendElixirWeb.Endpoint.config_change(changed, removed)
    :ok
  end
end
