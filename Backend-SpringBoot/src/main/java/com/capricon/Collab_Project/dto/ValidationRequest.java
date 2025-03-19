package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ValidationRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Please enter the verification code.")
    private String code;

}
