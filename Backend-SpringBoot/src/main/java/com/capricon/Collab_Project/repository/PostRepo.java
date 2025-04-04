package com.capricon.Collab_Project.repository;

import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepo extends JpaRepository<Post, UUID> {

    @Query(value = "SELECT * FROM posts WHERE id = :id", nativeQuery = true)
    Optional<Post> findByIdNative(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void decrementLikeCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount - 1 WHERE p.id = :postId AND p.commentCount > 0")
    void decrementCommentCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.repostCount = p.repostCount + 1 WHERE p.id = :postId")
    void incrementRepostCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.repostCount = p.repostCount - 1 WHERE p.id = :postId AND p.repostCount > 0")
    void decrementRepostCount(@Param("postId") UUID postId);

    @EntityGraph(attributePaths = {"comments", "likes", "reposts"})
    Optional<Post> findPostByAuthorId(User user);


    @EntityGraph(attributePaths = {"comments", "likes", "reposts"})
    Optional<Post> findByAuthorIdAndId(User user, UUID id);

}
