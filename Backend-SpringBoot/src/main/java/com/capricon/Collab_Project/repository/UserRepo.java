package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    //User repo allows us to interact with the database while working with User entity.
    //Jpa Repository provides built in methods for performing basic CRUD operations

    //Custom method to find user by their username
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

}
