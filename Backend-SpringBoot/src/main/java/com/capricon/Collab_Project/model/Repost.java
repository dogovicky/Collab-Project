package com.capricon.Collab_Project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "reposts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repost {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "repost_id")
    private UUID repostId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(name = "reposted_at")
    private Timestamp repostedAt;

}
