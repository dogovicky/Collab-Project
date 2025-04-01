package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    @NotBlank(message = "Post ID required")
    private UUID postId;

    @NotBlank(message = "Username required")
    private String username;

    @NotBlank(message = "Comment cannot be null text")
    private String commentText;

}
