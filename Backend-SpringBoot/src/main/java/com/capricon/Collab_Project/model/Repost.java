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
    private UUID repostId;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private Post postId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User userId;

    private Timestamp repostedAt;

}
