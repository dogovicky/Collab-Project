package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.CommentDTO;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.Comment;
import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.CommentRepo;
import com.capricon.Collab_Project.repository.PostRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final Executor executor;


    @Async
    public CompletableFuture<ApiResponse<Comment>> commentPost(CommentDTO commentDTO) {
        log.info("Calling comment method asynchronously");
        return CompletableFuture.completedFuture(comment(commentDTO))
                .exceptionally(this::handleCommentException);
    }

    @Async
    public CompletableFuture<ApiResponse<Object>> deleteComment(CommentDTO commentDTO) {
        log.info("Calling delete comment method");
//        return CompletableFuture.supplyAsync(() -> delete(commentDTO))
//                .exceptionally(this::handleCommentException);
        return CompletableFuture.completedFuture(delete(commentDTO))
                .exceptionally(this::handleCommentException);
    }

    @Transactional
    public ApiResponse<Comment> comment(CommentDTO commentDTO) {
        // Ensure user is valid
        User user = userRepo.findByUsername(commentDTO.getUsername())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        // Ensure post exists
        Post post = postRepo.findById(commentDTO.getPostId())
                .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

        // Check if user has already commented
        if (commentRepo.existsByUserAndPost(user, post)) {
            throw new BusinessException("User already commented on this post", HttpStatus.CONFLICT);
        }

        // Build and save comment
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .commentText(commentDTO.getCommentText())
                .build();
        commentRepo.save(comment);

        // Update comment count in post
        postRepo.incrementLikeCount(post.getId());

        return ApiResponse.success(comment, "Comment successful");
    }

    @Transactional
    public ApiResponse<Object> delete(CommentDTO commentDTO) {
        // Find user
        User user = userRepo.findByUsername(commentDTO.getUsername())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        // Find post
        Post post = postRepo.findById(commentDTO.getPostId())
                .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

        // Find if comment exists
        Comment comment = commentRepo.findByUserAndPost(user, post)
                .orElseThrow(() -> new BusinessException("Comment not found", HttpStatus.BAD_REQUEST));
        commentRepo.delete(comment);

        // Update comment count in post
        postRepo.decrementCommentCount(post.getId());
        return ApiResponse.success(null, "Comment deleted successfully");
    }

    private <T> ApiResponse<T> handleCommentException(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

        if (cause instanceof UserException userException) {
            return ApiResponse.error(userException.getStatus(), cause.getMessage());
        } else if (cause instanceof BusinessException businessException) {
            return ApiResponse.error(businessException.getStatus(), cause.getMessage());
        } else {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        }

    }

}
