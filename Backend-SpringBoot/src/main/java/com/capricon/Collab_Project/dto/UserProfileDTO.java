package com.capricon.Collab_Project.dto;

import com.capricon.Collab_Project.model.Post;
import com.capricon.Collab_Project.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    private String username;
    private String email;
    private String fullName;
    private String gender;
    private String bio;
    private String institution;
    private String phoneNumber;
    private List<String> fieldsOfInterest;

    public UserProfileDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.gender = user.getGender() != null ? String.valueOf(user.getGender()) : null;
        this.bio = user.getBio();
        this.institution = user.getInstitution();
        this.phoneNumber = user.getPhoneNumber();
        this.fieldsOfInterest = user.getFieldOfInterest() != null ?
                new ArrayList<>(user.getFieldOfInterest()) :
                new ArrayList<>();
    }

}
