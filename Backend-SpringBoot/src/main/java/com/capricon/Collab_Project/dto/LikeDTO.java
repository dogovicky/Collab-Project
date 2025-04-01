package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LikeDTO {

    //@NotBlank(message = "Post ID not supposed to be null")
    private String postId;

    @NotBlank(message = "User ID cannot be null")
    @Size(min = 3, max = 50, message = "Username must be 3 - 50 characters")
    private String username;

}
