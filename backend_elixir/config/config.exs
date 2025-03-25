import Config

# Configure Ecto Repo
config :backend_elixir,
  ecto_repos: [BackendElixir.Repo]

config :backend_elixir, BackendElixir.Repo, ssl: false

# Load RabbitMQ URL from environment or use the specified URL
rabbitmq_url =
  System.get_env("RABBITMQ_URL") ||
    "amqps://slzjfjxx:UsrrZc_Z1dWw2zz03GwpSGrRPDtrlzhX@cow.rmq2.cloudamqp.com:5671/slzjfjxx"

config :backend_elixir, BackendElixir.RabbitMQ, url: rabbitmq_url

# Import environment-specific configurations
import_config "#{Mix.env()}.exs"
