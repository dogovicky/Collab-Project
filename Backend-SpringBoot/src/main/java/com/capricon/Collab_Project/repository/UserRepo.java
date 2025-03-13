package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {

    //User repo allows us to interact with the database while working with User entity.
    //Jpa Repository provides built in methods for performing basic CRUD operations

    //Custom method to find user by their username
    User findByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

}
