package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.Comment;
import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepo extends JpaRepository<Comment, UUID> {

    Boolean existsByUserAndPost(User user, Post post);

    Optional<Comment> findByUserAndPost(User user, Post post);

}
