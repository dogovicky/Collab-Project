import Config

# Configure Ecto Repo
config :backend_elixir,
  ecto_repos: [BackendElixir.Repo]

config :backend_elixir, BackendElixir.Repo, ssl: false

# RabbitMQ Configuration
# config :backend_elixir, BackendElixir.RabbitMQ,
# amqp_url: System.get_env("RABBITMQ_URL") || "amqp://guest:guest@localhost:5672/"

# Import environment-specific configurations
import_config "#{Mix.env()}.exs"
