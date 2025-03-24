import Config

# Configure Ecto Repo
config :backend_elixir,
  ecto_repos: [BackendElixir.Repo]

config :backend_elixir, BackendElixir.Repo, ssl: false

# Load RabbitMQ URL from environment
rabbitmq_url = System.get_env("RABBITMQ_URL") || "amqps://your-default-url"

config :backend_elixir, BackendElixir.RabbitMQ, url: rabbitmq_url

# Import environment-specific configurations
import_config "#{Mix.env()}.exs"
