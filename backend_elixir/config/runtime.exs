import Config
require Logger

if System.get_env("PHX_SERVER") do
  config :backend_elixir, BackendElixirWeb.Endpoint, server: true
end

# RabbitMQ Configuration
rabbitmq_url =
  System.get_env("RABBITMQ_URL") ||
    "amqps://slzjfjxx:UsrrZc_Z1dWw2zz03GwpSGrRPDtrlzhX@cow.rmq2.cloudamqp.com:5671/slzjfjxx"

if is_nil(rabbitmq_url) or rabbitmq_url == "" do
  Logger.warning(" WARNING: RABBITMQ_URL is not set. Using default CloudAMQP URL.")
else
  Logger.info("Using RabbitMQ URL from environment.")
end

Application.put_env(:backend_elixir, :rabbitmq_url, rabbitmq_url)

# Debugging: Log RabbitMQ URL (without credentials)
rabbitmq_url_debug = String.replace(rabbitmq_url, ~r"//[^@]+@", "//[REDACTED]@")
Logger.info("RabbitMQ URL: #{rabbitmq_url_debug}")

# SSL Configuration for RabbitMQ
cacertfile_path = "/home/elon/cacert.pem"

if File.exists?(cacertfile_path) do
  Logger.info("CACert file found at #{cacertfile_path}")
else
  Logger.error("CACert file not found at #{cacertfile_path}")
end

if config_env() == :prod do
  database_url =
    System.get_env("DATABASE_URL") ||
      raise "ERROR: DATABASE_URL environment variable is missing."

  maybe_ipv6 = if System.get_env("ECTO_IPV6") in ~w(true 1), do: [:inet6], else: []

  config :backend_elixir, BackendElixir.Repo,
    url: database_url,
    pool_size: String.to_integer(System.get_env("POOL_SIZE") || "10"),
    socket_options: maybe_ipv6

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

# Ensure the variable is used to avoid the warning
_ = rabbitmq_url
