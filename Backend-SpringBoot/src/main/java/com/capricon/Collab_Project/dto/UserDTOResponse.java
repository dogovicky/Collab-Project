package com.capricon.Collab_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOResponse implements Serializable {

    private UserDTO userDTO;
    private String token;

}
