package com.capricon.Collab_Project.model;

import com.capricon.Collab_Project.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "connections")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Connection {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "connection_id")
    private UUID connectionId;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) //The initiator of the connection
    private User userId;

    @ManyToOne
    @JoinColumn(name = "connected_user_id", nullable = false) //The receiver of the connection
    private User connectedUserId;

    //@Column(columnDefinition = "connectionStatus")
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

}
