package com.capricon.Collab_Project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConnectionRequest {
    @NotBlank(message = "Please input your username")
    private String requestingUsername;

    @NotBlank(message = "Please input the username you want to connect to")
    private String receivingUsername;
}
