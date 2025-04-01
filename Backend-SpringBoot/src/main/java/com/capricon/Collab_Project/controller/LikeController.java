package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.LikeDTO;
import com.capricon.Collab_Project.model.Like;
import com.capricon.Collab_Project.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public CompletableFuture<ResponseEntity<ApiResponse<Like>>> likePost(@Valid @RequestBody LikeDTO likeDTO) {
        log.info("Like request called for post {} by user {}", likeDTO.getPostId(), likeDTO.getUsername());
        return likeService.like(likeDTO).thenApply(likeResponse -> {

            if (likeResponse.isSuccess()) {
                log.info("Like completed successfully for post {} by user {}",
                        likeDTO.getPostId(), likeDTO.getUsername());

                return ResponseEntity.ok(likeResponse);
            } else {
                log.error("Error liking post {} by user {}: {}",
                        likeDTO.getPostId(), likeDTO.getUsername(), likeResponse.getMessage());
                return ResponseEntity.status(likeResponse.getStatus()).body(likeResponse);
            }
        }).exceptionally(ex -> {
            Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

            log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"));
        });
    }

    @PatchMapping("/unlike")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> unlikePost(@Valid @RequestBody LikeDTO likeDTO) {
        log.info("Unlike request called for post {} by user {}", likeDTO.getPostId(), likeDTO.getUsername());

        return likeService.unlike(likeDTO).thenApply(response -> {
            if (response.isSuccess()) {
                log.info("Unlike completed successfully for post {} by user {}",
                        likeDTO.getPostId(), likeDTO.getUsername());

                return ResponseEntity.ok(response);
            } else {
                log.error("Error unliking post {} by user {}: {}",
                        likeDTO.getPostId(), likeDTO.getUsername(), response.getMessage());

                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }).exceptionally(ex -> {
            Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

            log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"));
        });
    }

}
