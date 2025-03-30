import Config

# Configure Ecto Repo
config :backend_elixir,
  ecto_repos: [BackendElixir.Repo]

config :backend_elixir, BackendElixir.Repo, ssl: false

config :backend_elixir, BackendElixirWeb.Endpoint, pubsub_server: BackendElixir.PubSub

# Ensure RabbitMQ URL is correctly pulled from the environment
config :backend_elixir, BackendElixir.RabbitMQ,
  url: System.get_env("RABBITMQ_URL") || raise("RABBITMQ_URL is not set")

# Import environment-specific configurations
import_config "#{Mix.env()}.exs"
