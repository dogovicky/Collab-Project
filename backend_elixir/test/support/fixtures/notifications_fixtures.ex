defmodule BackendElixir.NotificationsFixtures do
  @moduledoc """
  This module defines test helpers for creating
  entities via the `BackendElixir.Notifications` context.
  """

  @doc """
  Generate a notification.
  """
  def notification_fixture(attrs \\ %{}) do
    {:ok, notification} =
      attrs
      |> Enum.into(%{
        content: "some content",
        inserted_at: ~U[2025-03-16 23:46:00Z],
        read: true,
        type: "some type",
        user_id: 42
      })
      |> BackendElixir.Notifications.create_notification()

    notification
  end
end
