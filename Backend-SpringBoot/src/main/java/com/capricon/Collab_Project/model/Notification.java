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
    @Column(name = "notification_id")
    private UUID notificationId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User senderId;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiverId;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "notificationType")
    private NotificationType type;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "created_at")
    private Timestamp createdAt;

}
