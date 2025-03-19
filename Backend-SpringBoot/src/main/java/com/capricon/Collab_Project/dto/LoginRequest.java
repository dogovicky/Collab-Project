package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Please input your username")
    private String username;

    @NotBlank(message = "Enter your password")
    private String password;

}
