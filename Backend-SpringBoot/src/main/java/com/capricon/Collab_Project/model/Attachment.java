package com.capricon.Collab_Project.model;

import com.capricon.Collab_Project.model.enums.AttachmentType;
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
@Table(name = "attachments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "attachment_id")
    private UUID attachmentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", columnDefinition = "attachmentType")
    private AttachmentType fileType;

    @CreationTimestamp
    @Column(name = "uploaded_at")
    private Timestamp uploadedAt;

}
