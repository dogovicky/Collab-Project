defmodule BackendElixir.Repo.Migrations.CreateNotifications do
  use Ecto.Migration

  def change do
    create table(:notifications) do
      add :type, :string, null: false
      add :content, :text, null: false
      add :user_id, references(:users, type: :integer, on_delete: :delete_all)  # References Spring Boot's users table
      add :read, :boolean, default: false
      add :inserted_at, :utc_datetime, null: false
      timestamps()
    end
  end
end
