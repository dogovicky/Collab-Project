package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.RepostDTO;
import com.capricon.Collab_Project.model.Repost;
import com.capricon.Collab_Project.service.RepostService;
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
public class RepostController {

    private final RepostService repostService;

    @PostMapping("/repost")
    public CompletableFuture<ResponseEntity<ApiResponse<Repost>>> repost(@Valid @RequestBody RepostDTO repostDTO) {
        log.info("Repost request sent for post {} by user {}", repostDTO.getPostId(), repostDTO.getUsername());
        return repostService.repost(repostDTO)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        log.info("Successfully reposted post {}", repostDTO.getPostId());
                        return ResponseEntity.ok(response);
                    } else {
                        log.error("An error occurred while reposting post {}: {}",
                                repostDTO.getPostId(), response.getMessage());
                        return ResponseEntity.status(response.getStatus()).body(response);
                    }
                }).exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

                    log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                                    (cause != null) ? cause.getMessage() : "Unknown error"));
                });
    }


    @PatchMapping("/repost")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> deleteRepost(@Valid @RequestBody RepostDTO repostDTO) {
        log.info("Delete repost request sent for post {} by user {}", repostDTO.getPostId(), repostDTO.getUsername());
        return repostService.deletePost(repostDTO)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        log.info("Post successfully deleted");
                        return ResponseEntity.ok(response);
                    } else {
                        log.error("Error deleting post: {}", response.getMessage());
                        return ResponseEntity.status(response.getStatus()).body(response);
                    }
                }).exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

                    log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                                    (cause != null) ? cause.getMessage() : "Unknown error"));
                });
    }

}
