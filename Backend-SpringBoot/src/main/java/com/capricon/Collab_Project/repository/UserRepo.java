package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    //Custom method to find user by their username
    @EntityGraph
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @EntityGraph(attributePaths = {"posts"})
    Optional<User> findProfileDataByUsername(String username);

}
