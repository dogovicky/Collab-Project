package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.DeletePostDTO;
import com.capricon.Collab_Project.dto.EventDTO;
import com.capricon.Collab_Project.dto.PostDTO;
import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @GetMapping("/post/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> getPost(@PathVariable String username) {
        log.info("Fetching post for user: {}", username);
        try {
            ApiResponse<PostDTO> post = postService.getPostByUsername(username);
            if (post.isSuccess()) {
                log.info("Post fetched successfully for user: {}", username);
                return ResponseEntity.ok(post);
            } else {
                log.error("Error fetching post for user {}", username);
                return ResponseEntity.status(post.getStatus()).body(post);
            }
        } catch (Exception ex) {
            return handlePostControllerException(ex);
        }
    }

    @PostMapping("/create-post")
    public ResponseEntity<ApiResponse<PostDTO>> savePost(@RequestBody @Valid EventDTO eventDTO) {
        log.info("Creating post event");
        try {
            ApiResponse<PostDTO> response = postService.createEvent(eventDTO);
            if (response.isSuccess()) {
                log.info("Post created successfully by user {}", eventDTO.getUsername());
                return ResponseEntity.ok(response);
            } else {
                log.error("Error saving post, {}", response.getMessage());
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handlePostControllerException(ex);
        }

    }

    @DeleteMapping("/delete-post")
    public ResponseEntity<ApiResponse<Object>> deletePost(@Valid @RequestBody DeletePostDTO deletePostDTO) {
        log.info("Delete post request made for post {} by user {}", deletePostDTO.getPostId(), deletePostDTO.getUsername());
        try {
            ApiResponse<Object> response = postService.delete(deletePostDTO);
            if (response.isSuccess()) {
                log.info("Post successfully deleted");
                return ResponseEntity.ok(response);
            } else {
                log.error("Error deleting post");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handlePostControllerException(ex);
        }
    }

    public <T> ResponseEntity<ApiResponse<T>> handlePostControllerException(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred"));
    }

}
