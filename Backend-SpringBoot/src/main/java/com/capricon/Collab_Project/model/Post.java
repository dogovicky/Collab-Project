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
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "authorId", nullable = false)
    private User authorId;

    @ManyToOne
    @JoinColumn(name = "communityId", nullable = false)
    private Community communityId;

    private String label;

    private Timestamp createdAt;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<Like> likes;

    @OneToMany(mappedBy = "postId")
    private List<Repost> reposts;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<Attachment> attachments;
}
