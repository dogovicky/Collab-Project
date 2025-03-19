package com.capricon.Collab_Project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "communities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Community {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "community_name", unique = true, nullable = false)
    private String communityName;
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;


    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "communityId", cascade = CascadeType.ALL)
    private List<CommunityMembership> members;

    @OneToMany(mappedBy = "communityId", cascade = CascadeType.ALL)
    private List<Post> postList;

    protected void onCreate() {
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }

}
