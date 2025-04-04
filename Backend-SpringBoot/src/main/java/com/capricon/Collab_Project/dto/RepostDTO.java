package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class RepostDTO {

    private String postId;

    @NotBlank
    private String username;

    private String repostComment;

}
