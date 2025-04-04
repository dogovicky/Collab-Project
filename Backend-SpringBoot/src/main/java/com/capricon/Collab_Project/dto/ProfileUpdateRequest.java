package com.capricon.Collab_Project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequest {

    @NotBlank
    @Size(min = 3, max = 20, message = "Username must be between  3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    private String fullName;

    @Size(max = 255, message = "Bio must not exceed 255 characters")
    private String bio;

    @Size(max = 50, message = "Institution name must not exceed 50 characters")
    private String institution;

    private String gender;

    @Size(max = 100, message = "Fields of interest must not exceed 100 characters")
    private List<String> fieldsOfInterest;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
    private String phoneNumber;

}
