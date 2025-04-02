package com.capricon.Collab_Project.service;


import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.RepostDTO;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.Repost;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.PostRepo;
import com.capricon.Collab_Project.repository.RepostRepo;
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
public class RepostService {

    private final RepostRepo repostRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;

    // A user can repost as many times as they want
    @Transactional
    public ApiResponse<RepostDTO> saveRepost(RepostDTO repostDTO) {
        try {
            // Find user
            User user = userRepo.findByUsername(repostDTO.getUsername())
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            // Find Post
            UUID postId = UUID.fromString(repostDTO.getPostId());
            Post post = postRepo.findByIdNative(postId)
                    .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

            // Build the repost
            Repost repost = Repost.builder()
                    .post(post)
                    .user(user)
                    .repostComment(repostDTO.getRepostComment() != null ? repostDTO.getRepostComment() : null)
                    .build();
            repostRepo.save(repost);

            // Update repost count
            postRepo.incrementRepostCount(post.getId());

            return ApiResponse.success(buildRepostResponse(repost), "Successfully reposted");
        } catch (Exception ex) {
            return handleRepostException(ex);
        }
    }

    @Transactional
    public ApiResponse<Object> deleteRepost(RepostDTO repostDTO) {
        try {
            // Find user
            User user = userRepo.findByUsername(repostDTO.getUsername())
                    .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

            // Find Post
            Post post = postRepo.findByIdNative(UUID.fromString(repostDTO.getPostId()))
                    .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));

            //Check if the repost exists and delete
            Repost repost = repostRepo.findByUserAndPost(user, post)
                    .orElseThrow(() -> new BusinessException("Post not found", HttpStatus.NOT_FOUND));
            repostRepo.delete(repost);

            // Update count in post
            postRepo.decrementRepostCount(post.getId());
            return ApiResponse.success(null, "Successfully deleted post");
        } catch (Exception ex) {
            return handleRepostException(ex);
        }
    }


    private <T> ApiResponse<T> handleRepostException(Throwable ex) {
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

    private RepostDTO buildRepostResponse(Repost repost) {
        return RepostDTO.builder()
                .postId(repost.getRepostId().toString())
                .username(repost.getUser().getUsername())
                .repostComment(repost.getRepostComment())
                .build();
    }
}
