package com.capricon.Collab_Project.dto;

import com.capricon.Collab_Project.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
