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

    @Column(name = "joinedAt")
    private Timestamp joinedAt;

    @ManyToOne
    @JoinColumn(name = "communityId", nullable = false, referencedColumnName = "uuid")
    private Community communityId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, referencedColumnName = "UUID")
    private User userId;

}
