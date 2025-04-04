package com.capricon.Collab_Project.model;

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
@Table(name = "reposts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Repost {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "repost_id")
    private UUID repostId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String repostComment;

    @CreationTimestamp
    @Column(name = "reposted_at")
    private Timestamp repostedAt;

}
