package com.capricon.Collab_Project.model;

import com.capricon.Collab_Project.model.enums.AttachmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "attachments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID attachmentId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post postId;

    @ManyToOne
    @JoinColumn(name = "messageId")
    private Message messageId;

    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "attachmentType")
    private AttachmentType fileType;

    private Timestamp uploadedAt;

}
