import Config

# Ensure the Phoenix server starts when required
if System.get_env("PHX_SERVER") do
  config :backend_elixir, BackendElixirWeb.Endpoint, server: true
end

# RabbitMQ Configuration (Moved outside `if config_env() == :prod do`)
rabbitmq_url =
  System.get_env("RABBITMQ_URL") ||
    "amqps://slzjfjxx:UsrrZc_Z1dWw2zz03GwpSGrRPDtrlzhX@cow.rmq2.cloudamqp.com:5672"

if is_nil(rabbitmq_url) or rabbitmq_url == "" do
  raise "environment variable RABBITMQ_URL is missing or empty."
end

config :backend_elixir, BackendElixir.RabbitMQ, amqp_url: rabbitmq_url

if config_env() == :prod do
  # Database Configuration
  database_url =
    System.get_env("DATABASE_URL") ||
      raise """
      environment variable DATABASE_URL is missing.
      For example: ecto://USER:PASS@HOST/DATABASE
      """

  maybe_ipv6 = if System.get_env("ECTO_IPV6") in ~w(true 1), do: [:inet6], else: []

  config :backend_elixir, BackendElixir.Repo,
    url: database_url,
    pool_size: String.to_integer(System.get_env("POOL_SIZE") || "10"),
    socket_options: maybe_ipv6

  # Secret Key Base Configuration
  secret_key_base =
    System.get_env("SECRET_KEY_BASE") ||
      raise """
      environment variable SECRET_KEY_BASE is missing.
      You can generate one by calling: mix phx.gen.secret
      """

  host = System.get_env("PHX_HOST") || "example.com"
  port = String.to_integer(System.get_env("PORT") || "4000")

  config :backend_elixir, BackendElixirWeb.Endpoint,
    url: [host: host, port: 443, scheme: "https"],
    http: [
      ip: {0, 0, 0, 0, 0, 0, 0, 0},
      port: port
    ],
    secret_key_base: secret_key_base
end
