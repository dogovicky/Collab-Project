package com.capricon.Collab_Project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private User senderId;

    @ManyToOne
    @JoinColumn(name = "receiverId", nullable = false)
    private User receiverId;

    private String messageText;

    private Timestamp sentAt;

    private Timestamp readAt;

    @OneToMany(mappedBy = "messageId", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

}
