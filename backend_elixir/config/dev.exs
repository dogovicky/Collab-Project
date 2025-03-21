import Config

# Configure your database
config :backend_elixir, BackendElixir.Repo,
  username: "postgres.wcmigjlaaoplbydadlzx",
  password: "Coll@bproject2025",
  hostname: "aws-0-eu-central-1.pooler.supabase.com",
  database: "postgres",
  stacktrace: true,
  ssl: false,
  parameters: [sslmode: "require"],
  show_sensitive_data_on_connection_error: true,
  pool_size: 15

# Configure RabbitMQ
config :backend_elixir, BackendElixir.RabbitMQ,
  url: System.get_env("RABBITMQ_URL") || "amqp://guest:guest@localhost:5672"

# Phoenix Endpoint Configuration
config :backend_elixir, BackendElixirWeb.Endpoint,
  http: [ip: {127, 0, 0, 1}, port: 4000],
  check_origin: false,
  code_reloader: true,
  debug_errors: true,
  secret_key_base: "OUFSso0UMiDBT45urviE4tXqHHq8n8UtokUfmOVRuDQpbrGrteIm/s9zL+BBGdxP",
  watchers: []

# Enable dev routes for dashboard and mailbox
config :backend_elixir, dev_routes: true

# Logging configuration
config :logger, :console, format: "[$level] $message\n"

# Set stacktrace depth
config :phoenix, :stacktrace_depth, 20

# Initialize plugs at runtime
config :phoenix, :plug_init_mode, :runtime

# Disable swoosh api client
config :swoosh, :api_client, false
