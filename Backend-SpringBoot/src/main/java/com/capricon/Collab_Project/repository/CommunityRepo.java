package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommunityRepo extends JpaRepository<Community, UUID> {
}
