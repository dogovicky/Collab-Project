import Config

# Configure Ecto Repo
config :backend_elixir,
  ecto_repos: [BackendElixir.Repo]

config :backend_elixir, BackendElixir.Repo, ssl: false

config :backend_elixir, BackendElixirWeb.Endpoint,
  pubsub_server: BackendElixir.PubSub,
  url: [host: "192.168.155.172", port: 4000],
  # Allows external connections
  http: [ip: {0, 0, 0, 0}, port: 4000],
  # Allows frontend requests from any domain (useful for local development)
  check_origin: false

# Ensure RabbitMQ URL is correctly pulled from the environment
config :backend_elixir, BackendElixir.RabbitMQ,
  url: System.get_env("RABBITMQ_URL") || raise("RABBITMQ_URL is not set")

# Import environment-specific configurations
import_config "#{Mix.env()}.exs"
