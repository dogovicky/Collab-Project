package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.CommentDTO;
import com.capricon.Collab_Project.model.Comment;
import com.capricon.Collab_Project.service.CommentService;
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
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<ApiResponse<CommentDTO>> comment(@Valid @RequestBody CommentDTO commentDTO) {
        log.info("Commenting on post {} by user {}", commentDTO.getPostId(), commentDTO.getUsername());
        try {
            ApiResponse<CommentDTO> response = commentService.comment(commentDTO);
            if (response.isSuccess()) {
                log.info("Successfully commented on post {} by user {}", commentDTO.getPostId(), commentDTO.getUsername());
                return ResponseEntity.ok(response);
            } else {
                log.error("Failed to comment on post {} by user {}", commentDTO.getPostId(), commentDTO.getUsername());
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

            log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"));
        }
    }


    @PatchMapping("/comment")
    public ResponseEntity<ApiResponse<Object>> delete(@Valid @RequestBody CommentDTO commentDTO) {
        log.info("Deleting comment for post {} by user {}", commentDTO.getPostId(), commentDTO.getUsername());
        try {
            ApiResponse<Object> response = commentService.delete(commentDTO);
            if (response.isSuccess()) {
                log.info("Successfully deleted comment");
                return ResponseEntity.ok(response);
            } else {
                log.error("Failed to delete comment: {}", response.getMessage());
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

            log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"));
        }
    }

}
