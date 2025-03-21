defmodule BackendElixir.Notifications.Notification do
  use Ecto.Schema
  import Ecto.Changeset

  schema "notification" do
    field :type, :string
    field :read, :boolean, default: false
    field :content, :string
    field :user_id, :integer

    timestamps()
  end

  @doc false
  def changeset(notification, attrs) do
    notification
    |> cast(attrs, [:type, :content, :user_id, :read, :inserted_at])
    |> validate_required([:type, :content, :user_id, :read, :inserted_at])
  end
end
