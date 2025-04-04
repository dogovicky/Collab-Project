package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    //@NotBlank(message = "Post ID required")
    private String postId;

    @NotBlank(message = "Username required")
    private String username;

    @NotBlank(message = "Comment cannot be null text")
    private String commentText;

    @CreationTimestamp
    private Timestamp timestamp;

}
