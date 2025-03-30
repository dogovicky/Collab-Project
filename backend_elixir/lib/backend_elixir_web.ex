defmodule BackendElixirWeb do
  @moduledoc """
  The entry point for defining your web interface, such
  as controllers, components, channels, and so on.

  This can be used in your application as:

      use BackendElixirWeb, :controller
      use BackendElixirWeb, :html

  The definitions below will be executed for every controller,
  component, etc., so keep them short and clean, focused
  on imports, uses, and aliases.

  Do NOT define functions inside the quoted expressions
  below. Instead, define additional modules and import
  those modules here.
  """

  def static_paths, do: ~w(assets fonts images favicon.ico robots.txt)

  ## Router definition
  def router do
    quote do
      use Phoenix.Router, helpers: false

      # Import common connection and controller functions to use in pipelines
      import Plug.Conn
      import Phoenix.Controller
    end
  end

  ## Channel definition
  def channel do
    quote do
      use Phoenix.Channel
    end
  end

  ##  Controller definition
  def controller do
    quote do
      use Phoenix.Controller,
        formats: [:html, :json],
        layouts: [html: BackendElixirWeb.Layouts]

      import Plug.Conn
      import BackendElixirWeb.Gettext
      unquote(verified_routes())
    end
  end

  ##  HTML-related imports
  def html do
    quote do
      use Phoenix.Component
      import Phoenix.HTML
      import BackendElixirWeb.Gettext
      unquote(verified_routes())
    end
  end

  ##  Verified Routes for path helpers
  def verified_routes do
    quote do
      use Phoenix.VerifiedRoutes,
        endpoint: BackendElixirWeb.Endpoint,
        router: BackendElixirWeb.Router,
        statics: BackendElixirWeb.static_paths()
    end
  end

  @doc """
  When used, dispatch to the appropriate controller/live_view/etc.
  """
  defmacro __using__(which) when is_atom(which) do
    apply(__MODULE__, which, [])
  end
end
