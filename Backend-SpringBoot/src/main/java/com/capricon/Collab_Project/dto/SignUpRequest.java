package com.capricon.Collab_Project.dto;


import com.capricon.Collab_Project.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class SignUpRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$")
    private String password;

    private List<String> fieldOfInterest;

    private Gender gender;

    private String institution;
    private String bio;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}
