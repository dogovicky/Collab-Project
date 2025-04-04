defmodule BackendElixir.ChatTest do
  use BackendElixir.DataCase

  alias BackendElixir.Chat

  describe "messages" do
    alias BackendElixir.Chat.Message

    import BackendElixir.ChatFixtures

    @invalid_attrs %{context: nil, user_id: nil, group_id: nil, inserted_at: nil}

    test "list_messages/0 returns all messages" do
      message = message_fixture()
      assert Chat.list_messages() == [message]
    end

    test "get_message!/1 returns the message with given id" do
      message = message_fixture()
      assert Chat.get_message!(message.id) == message
    end

    test "create_message/1 with valid data creates a message" do
      valid_attrs = %{context: "some context", user_id: 42, group_id: 42, inserted_at: ~U[2025-03-16 22:17:00Z]}

      assert {:ok, %Message{} = message} = Chat.create_message(valid_attrs)
      assert message.context == "some context"
      assert message.user_id == 42
      assert message.group_id == 42
      assert message.inserted_at == ~U[2025-03-16 22:17:00Z]
    end

    test "create_message/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Chat.create_message(@invalid_attrs)
    end

    test "update_message/2 with valid data updates the message" do
      message = message_fixture()
      update_attrs = %{context: "some updated context", user_id: 43, group_id: 43, inserted_at: ~U[2025-03-17 22:17:00Z]}

      assert {:ok, %Message{} = message} = Chat.update_message(message, update_attrs)
      assert message.context == "some updated context"
      assert message.user_id == 43
      assert message.group_id == 43
      assert message.inserted_at == ~U[2025-03-17 22:17:00Z]
    end

    test "update_message/2 with invalid data returns error changeset" do
      message = message_fixture()
      assert {:error, %Ecto.Changeset{}} = Chat.update_message(message, @invalid_attrs)
      assert message == Chat.get_message!(message.id)
    end

    test "delete_message/1 deletes the message" do
      message = message_fixture()
      assert {:ok, %Message{}} = Chat.delete_message(message)
      assert_raise Ecto.NoResultsError, fn -> Chat.get_message!(message.id) end
    end

    test "change_message/1 returns a message changeset" do
      message = message_fixture()
      assert %Ecto.Changeset{} = Chat.change_message(message)
    end
  end
end
