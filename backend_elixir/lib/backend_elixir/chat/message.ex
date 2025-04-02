defmodule BackendElixir.Chat.Message do
  use Ecto.Schema
  import Ecto.Changeset

  schema "messages" do
    field :context, :string
    field :user_id, :integer
    field :group_id, :integer
   

    timestamps(type: :utc_datetime)
  end

  @doc false
  def changeset(message, attrs) do
    message
    |> cast(attrs, [:context, :user_id, :group_id, :inserted_at])
    |> validate_required([:context, :user_id, :group_id, :inserted_at])
  end
end
