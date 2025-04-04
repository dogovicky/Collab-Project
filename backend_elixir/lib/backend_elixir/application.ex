defmodule BackendElixir.Application do
  @moduledoc false

  use Application

  @impl true
  def start(_type, _args) do
    rabbitmq_url = Application.fetch_env!(:backend_elixir, BackendElixir.RabbitMQ)[:url]

    children = [
      BackendElixirWeb.Telemetry,
      BackendElixir.Repo,
      BackendElixir.RabbitMQ,
      {DNSCluster, query: Application.get_env(:backend_elixir, :dns_cluster_query) || :ignore},
      {Phoenix.PubSub, name: BackendElixir.PubSub},
      {Finch, name: BackendElixir.Finch},
      BackendElixirWeb.Endpoint,

      # Pass RabbitMQ URL to the consumer
      {BackendElixir.Messaging.Consumer, url: rabbitmq_url}
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
