package com.capricon.Collab_Project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ConnectionDTO {
    private String requestingUser;
    private String receivingUser;
    private String status;
}
