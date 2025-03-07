package com.capricon.Collab_Project.model;

import com.capricon.Collab_Project.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID notificationId;

    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private User senderId;

    @ManyToOne
    @JoinColumn(name = "receiverId", nullable = false)
    private User receiverId;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "notificationType")
    private NotificationType type;

    private boolean isRead;

    private UUID referenceId;

    private Timestamp createdAt;

}
