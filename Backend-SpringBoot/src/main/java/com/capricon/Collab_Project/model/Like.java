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
@Table(name = "likes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "like_id")
    private UUID likeId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
