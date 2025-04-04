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

import java.util.UUID;
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


    @Transactional(timeout = 10)
    public ApiResponse<CommentDTO> comment(CommentDTO commentDTO) {
        try {
            // Ensure user is valid
            User user = userRepo.findByUsername(commentDTO.getUsername())
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            // Ensure post exists
            UUID postId = UUID.fromString(commentDTO.getPostId());
            log.info("Post id is {}", postId);
            Post post = postRepo.findByIdNative(postId)
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
            postRepo.incrementCommentCount(post.getId());

            return ApiResponse.success(buildCommentResponse(comment), "Comment successful");
        } catch (Exception ex) {
            return handleCommentException(ex);
        }
    }

    @Transactional(timeout = 10)
    public ApiResponse<Object> delete(CommentDTO commentDTO) {
        try {
            // Find user
            User user = userRepo.findByUsername(commentDTO.getUsername())
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            // Find post
            Post post = postRepo.findByIdNative(UUID.fromString(commentDTO.getPostId()))
                    .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

            // Find if comment exists
            Comment comment = commentRepo.findByUserAndPost(user, post)
                    .orElseThrow(() -> new BusinessException("Comment not found", HttpStatus.BAD_REQUEST));
            commentRepo.delete(comment);

            // Update comment count in post
            postRepo.decrementCommentCount(post.getId());
            return ApiResponse.success(null, "Comment deleted successfully");
        } catch (Exception ex) {
            return handleCommentException(ex);
        }
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

    private CommentDTO buildCommentResponse(Comment comment) {
        return CommentDTO.builder()
                .postId(comment.getPost().getId().toString())
                .commentText(comment.getCommentText())
                .username(comment.getUser().getUsername())
                .build();
    }

}
