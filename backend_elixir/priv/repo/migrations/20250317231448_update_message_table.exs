defmodule BackendElixir.Repo.Migrations.UpdateMessageTable do
  use Ecto.Migration

  def change do
 create table(:messages) do
      add :content, :text, null: false
      add :user_id, references(:users, type: :integer, on_delete: :delete_all)  # References Spring Boot's users table
      add :group_id, references(:groups, type: :integer, on_delete: :delete_all)  # References Spring Boot's groups table
      add :inserted_at, :utc_datetime, null: false

      timestamps()
    end

    # alter table(:messages) do
    # add :sent_at, :utc_datetime
    # end
  end
end
