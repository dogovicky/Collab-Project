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

    @Column(name = "communityName", unique = true, nullable = false)
    private String communityName;
    private String description;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private Timestamp createdAt;


    @ManyToOne
    @JoinColumn(name = "UUID", nullable = false, referencedColumnName = "UUID")
    private User createdBy;

    @OneToMany(mappedBy = "communityId", cascade = CascadeType.ALL)
    private List<CommunityMembership> members;

    @OneToMany(mappedBy = "communityId", cascade = CascadeType.ALL)
    private List<Post> postList;

    protected void onCreate() {
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }

}
