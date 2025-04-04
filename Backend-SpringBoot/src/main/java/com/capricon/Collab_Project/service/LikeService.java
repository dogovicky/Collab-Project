package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.LikeDTO;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.Like;
import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.LikeRepo;
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
public class LikeService {

    private final LikeRepo likeRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;


    @Transactional
    public ApiResponse<LikeDTO> likePost(LikeDTO likeDTO) {
       try {
           // Find user
           User user = userRepo.findByUsername(likeDTO.getUsername())
                   .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

           // Find post
           UUID postId = UUID.fromString(likeDTO.getPostId());
           Post post = postRepo.findByIdNative(postId)
                   .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

           // Check for existing like
           if (likeRepo.existsByUserAndPost(user, post)) {
               throw new BusinessException("User already liked post", HttpStatus.CONFLICT);
           }

           // Save Like
           Like like = Like.builder()
                   .post(post)
                   .user(user)
                   .build();

           likeRepo.save(like);

           // Update like count
           postRepo.incrementLikeCount(post.getId());

           return ApiResponse.success(buildLikeResponse(like), "Successfully liked");
       } catch (Exception ex) {
           return handleLikeException(ex);
       }
    }

    @Transactional
    public ApiResponse<Object> unlikePost(LikeDTO likeDTO) {
        try {
            // Find user
            User user = userRepo.findByUsername(likeDTO.getUsername())
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            // Find post
            Post post = postRepo.findByIdNative(UUID.fromString(likeDTO.getPostId()))
                    .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

            // Check if like exists
            Like like = likeRepo.findByUserAndPost(user, post)
                    .orElseThrow(() -> new BusinessException("Post not liked by user", HttpStatus.BAD_REQUEST));
            likeRepo.delete(like);

            // Update like count
            postRepo.decrementLikeCount(post.getId());

            return ApiResponse.success(null, "Unlike successful");
        } catch (Exception ex) {
            return handleLikeException(ex);
        }
    }

    // Extract common exception handling
    private <T> ApiResponse<T> handleLikeException(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

        if (cause instanceof UserException userException) {
            return ApiResponse.error(userException.getStatus(), userException.getMessage());
        } else if (cause instanceof BusinessException businessException) {
            return ApiResponse.error(businessException.getStatus(), businessException.getMessage());
        } else {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }


    private LikeDTO buildLikeResponse(Like like) {
        return LikeDTO.builder()
                .postId(like.getPost().getId().toString())
                .username(like.getUser().getUsername())
                .build();
    }

}
