package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeletePostDTO {

    @NotBlank
    private UUID postId;

    @NotBlank
    private String username;

}
