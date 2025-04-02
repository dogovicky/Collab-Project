package com.capricon.Collab_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProfileRequest {
    private UserProfileDTO userProfileDTO;
    private List<PostDTO> posts;
}
