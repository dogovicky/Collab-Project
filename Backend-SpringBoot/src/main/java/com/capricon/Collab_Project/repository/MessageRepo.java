package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.Message;
import com.capricon.Collab_Project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepo extends JpaRepository<Message, UUID> {
    // Find messages between two users
    List<Message> findBySenderIdAndReceiverId(User senderId, User receiverId);

    // Find all messages received by a user
    List<Message> findByReceiverId(User receiverId);

    // Optional: find all messages sent by a user
    List<Message> findBySenderId(User senderId);

    @Query(value = "SELECT DISTINCT receiver_id FROM message WHERE sender_id = (SELECT id FROM users WHERE username = :username) " +
            "UNION " +
            "SELECT DISTINCT sender_id FROM message WHERE receiver_id = (SELECT id FROM users WHERE username = :username)", nativeQuery = true)
    List<UUID> findConversationUserIds(@Param("username") String username);

    @Query("SELECT DISTINCT m.receiverId.username FROM Message m WHERE m.senderId.username = :username " +
            "UNION " +
            "SELECT DISTINCT m.senderId.username FROM Message m WHERE m.receiverId.username = :username")
    List<String> findConversationPartners(@Param("username") String username);


}
