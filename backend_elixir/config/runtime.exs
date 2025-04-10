import Config
require Logger

# Enable Phoenix server if PHX_SERVER is set
if System.get_env("PHX_SERVER") do
  config :backend_elixir, BackendElixirWeb.Endpoint, server: true
end

# RabbitMQ Configuration
rabbitmq_url =
  System.get_env("RABBITMQ_URL") ||
    raise "ERROR: RABBITMQ_URL is not set. Please export it before running the app."

# Log RabbitMQ URL for debugging (without exposing credentials)
rabbitmq_url_debug = String.replace(rabbitmq_url, ~r"//[^@]+@", "//[REDACTED]@")
Logger.info("Using RabbitMQ URL: #{rabbitmq_url_debug}")

# Store RabbitMQ URL in the application environment
Application.put_env(:backend_elixir, :rabbitmq_url, rabbitmq_url)

# RabbitMQ SSL Configuration
rabbitmq_host = System.get_env("RABBITMQ_HOST") || raise "ERROR: RABBITMQ_HOST is not set!"
cacertfile_path = "/home/vicky/Java Web/Collab-Project/backend_elixir/cacert.pem"

unless File.exists?(cacertfile_path) do
  Logger.error(
    "CACert file not found at #{cacertfile_path}. Ensure it exists for SSL verification."
  )
end

Application.put_env(:backend_elixir, :rabbitmq_ssl_options,
  verify: :verify_peer,
  cacertfile: cacertfile_path,
  server_name_indication: rabbitmq_host
)

# Database Configuration
database_url =
  System.get_env("DATABASE_URL") ||
    raise "ERROR: DATABASE_URL is not set. Please export it before running the app."

# Log database URL without exposing credentials
Logger.info(
  "Using Database URL: #{String.replace(database_url, ~r":\/\/[^:]+:[^@]+@", "://[REDACTED]@")}"
)

config :backend_elixir, BackendElixir.Repo,
  url: database_url,
  pool_size: String.to_integer(System.get_env("POOL_SIZE") || "10"),
  socket_options: if(System.get_env("ECTO_IPV6") in ~w(true 1), do: [:inet6], else: [:inet])

# Production-specific configuration
if config_env() == :prod do
  secret_key_base =
    System.get_env("SECRET_KEY_BASE") ||
      raise "ERROR: SECRET_KEY_BASE is missing. Generate one using: mix phx.gen.secret"

  host = System.get_env("PHX_HOST") || "example.com"
  port = String.to_integer(System.get_env("PORT") || "4000")

  config :backend_elixir, BackendElixirWeb.Endpoint,
    url: [host: host, port: 443, scheme: "https"],
    http: [ip: {0, 0, 0, 0, 0, 0, 0, 0}, port: port],
    secret_key_base: secret_key_base
end

# Prevent compiler warnings for unused variables
_ = rabbitmq_url
_ = database_url
_ = rabbitmq_host
