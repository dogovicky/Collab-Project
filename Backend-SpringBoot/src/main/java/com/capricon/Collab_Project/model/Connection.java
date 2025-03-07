package com.capricon.Collab_Project.model;

import com.capricon.Collab_Project.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "connections")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Connection {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID connectionId;


    @ManyToOne
    @JoinColumn(name = "userId", nullable = false) //The initiator of the connection
    private User userId;

    @ManyToOne
    @JoinColumn(name = "connectedUserId", nullable = false) //The receiver of the connection
    private User connectedUserId;

    @Column(columnDefinition = "connectionStatus")
    @Enumerated(EnumType.STRING)
    private Status status;

    private Timestamp createdAt;

}
