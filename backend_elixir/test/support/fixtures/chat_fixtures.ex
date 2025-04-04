defmodule BackendElixir.ChatFixtures do
  @moduledoc """
  This module defines test helpers for creating
  entities via the `BackendElixir.Chat` context.
  """

  @doc """
  Generate a message.
  """
  def message_fixture(attrs \\ %{}) do
    {:ok, message} =
      attrs
      |> Enum.into(%{
        context: "some context",
        group_id: 42,
        inserted_at: ~U[2025-03-16 22:17:00Z],
        user_id: 42
      })
      |> BackendElixir.Chat.create_message()

    message
  end
end
