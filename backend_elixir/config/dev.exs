import Config

# Configure your database
config :backend_elixir, BackendElixir.Repo,
  username: System.get_env("DB_USERNAME") || raise("DB_USERNAME is not set!"),
  password: System.get_env("DB_PASSWORD") || raise("DB_PASSWORD is not set!"),
  hostname: System.get_env("DB_HOSTNAME") || raise("DB_HOSTNAME is not set!"),
  database: System.get_env("DB_NAME") || raise("DB_NAME is not set!"),
  stacktrace: true,
  ssl: false,
  parameters: [sslmode: "require"],
  show_sensitive_data_on_connection_error: true,
  pool_size: 15,
  timeout: 60_000,
  queue_target: 50_000,
  queue_interval: 1_000

# Configure RabbitMQ
config :backend_elixir, BackendElixir.RabbitMQ,
  url: System.get_env("RABBITMQ_URL") || raise("RABBITMQ_URL is not set!")

# Phoenix Endpoint Configuration
config :backend_elixir, BackendElixirWeb.Endpoint,
  http: [ip: {127, 0, 0, 1}, port: 4000],
  check_origin: false,
  code_reloader: true,
  debug_errors: true,
  secret_key_base: System.get_env("SECRET_KEY_BASE") || raise("SECRET_KEY_BASE is not set!"),
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
