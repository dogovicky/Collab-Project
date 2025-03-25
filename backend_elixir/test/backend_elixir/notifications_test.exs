defmodule BackendElixir.NotificationsTest do
  use BackendElixir.DataCase

  alias BackendElixir.Notifications

  describe "notification" do
    alias BackendElixir.Notifications.Notification

    import BackendElixir.NotificationsFixtures

    @invalid_attrs %{type: nil, read: nil, content: nil, user_id: nil, inserted_at: nil}

    test "list_notification/0 returns all notification" do
      notification = notification_fixture()
      assert Notifications.list_notification() == [notification]
    end

    test "get_notification!/1 returns the notification with given id" do
      notification = notification_fixture()
      assert Notifications.get_notification!(notification.id) == notification
    end

    test "create_notification/1 with valid data creates a notification" do
      valid_attrs = %{type: "some type", read: true, content: "some content", user_id: 42, inserted_at: ~U[2025-03-16 23:46:00Z]}

      assert {:ok, %Notification{} = notification} = Notifications.create_notification(valid_attrs)
      assert notification.type == "some type"
      assert notification.read == true
      assert notification.content == "some content"
      assert notification.user_id == 42
      assert notification.inserted_at == ~U[2025-03-16 23:46:00Z]
    end

    test "create_notification/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Notifications.create_notification(@invalid_attrs)
    end

    test "update_notification/2 with valid data updates the notification" do
      notification = notification_fixture()
      update_attrs = %{type: "some updated type", read: false, content: "some updated content", user_id: 43, inserted_at: ~U[2025-03-17 23:46:00Z]}

      assert {:ok, %Notification{} = notification} = Notifications.update_notification(notification, update_attrs)
      assert notification.type == "some updated type"
      assert notification.read == false
      assert notification.content == "some updated content"
      assert notification.user_id == 43
      assert notification.inserted_at == ~U[2025-03-17 23:46:00Z]
    end

    test "update_notification/2 with invalid data returns error changeset" do
      notification = notification_fixture()
      assert {:error, %Ecto.Changeset{}} = Notifications.update_notification(notification, @invalid_attrs)
      assert notification == Notifications.get_notification!(notification.id)
    end

    test "delete_notification/1 deletes the notification" do
      notification = notification_fixture()
      assert {:ok, %Notification{}} = Notifications.delete_notification(notification)
      assert_raise Ecto.NoResultsError, fn -> Notifications.get_notification!(notification.id) end
    end

    test "change_notification/1 returns a notification changeset" do
      notification = notification_fixture()
      assert %Ecto.Changeset{} = Notifications.change_notification(notification)
    end
  end
end
