package com.capricon.Collab_Project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "community_memberships")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityMembership {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @Column(name = "joined_at")
    private Timestamp joinedAt;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false, referencedColumnName = "uuid")
    private Community communityId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "UUID")
    private User userId;

}
