package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.Connection;
import com.capricon.Collab_Project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRepo extends JpaRepository<Connection, UUID> {

    boolean existsByUserIdAndConnectedUserId(User user, User connectedUser);

    Optional<Connection> findByUserIdAndConnectedUserId(User user, User connectedUser);

    // Find pending requests sent by the user
    @Query("SELECT c FROM Connection c WHERE c.userId.uuid =" +
            " (SELECT u.uuid FROM User u WHERE u.username = :username) AND c.status = 'PENDING'")
    List<Connection> findAllPendingRequestsSentByUserId(@Param("username") String username);

    // Find pending requests received by the user
    @Query("SELECT c FROM Connection c WHERE c.connectedUserId.uuid =" +
            " (SELECT u.uuid FROM User u WHERE u.username = :username) AND c.status = 'PENDING'")
    List<Connection> findAllPendingRequestsReceivedByUserId(@Param("username") String username);


}
