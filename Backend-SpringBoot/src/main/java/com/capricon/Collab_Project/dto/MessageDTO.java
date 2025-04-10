package com.capricon.Collab_Project.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String senderUsername;
    private String recipientUsername;
    private String messageText;
    private Timestamp timestamp;

    public MessageDTO(String username, String username1, String messageText) {
        this.senderUsername = username;
        this.recipientUsername = username1;
        this.messageText = messageText;
    }
}
