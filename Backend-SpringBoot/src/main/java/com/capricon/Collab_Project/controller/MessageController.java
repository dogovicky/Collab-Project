package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.MessageDTO;
import com.capricon.Collab_Project.model.Message;
import com.capricon.Collab_Project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    //private final UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        Message message = messageService.sendMessage(
                messageDTO.getSenderUsername(),
                messageDTO.getRecipientUsername(),
                messageDTO.getMessageText()
        );

        MessageDTO responseDTO = MessageDTO.builder()
                .senderUsername(message.getSenderId().getUsername())
                .recipientUsername(message.getReceiverId().getUsername())
                .messageText(message.getMessageText())
                .timestamp(message.getSentAt())
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/chat")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessages(
            @RequestParam String sender,
            @RequestParam String recipient
    ) {
        List<MessageDTO> messages = messageService.getChat(sender, recipient);

        // You can customize this message depending on the outcome
        ApiResponse<List<MessageDTO>> response = new ApiResponse<>(
                "success",
                "Messages fetched successfully",
                messages
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/conversations/{username}")
    public ResponseEntity<List<String>> getConversations(@PathVariable String username) {
        List<String> conversations = messageService.getConversations(username);
        return ResponseEntity.ok(conversations);
    }



}


