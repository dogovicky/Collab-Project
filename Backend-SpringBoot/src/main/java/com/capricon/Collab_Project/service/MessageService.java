package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.MessageDTO;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.Message;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.MessageRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepo messageRepository;
    private final UserRepo userRepo;

    public Message sendMessage(String senderUsername, String recipientUsername, String messageText) {
        // Fetch sender and recipient as User entities
        User sender = userRepo.findByUsername(senderUsername)
                .orElseThrow(() -> new UserException("Sender not found", HttpStatus.NOT_FOUND));

        User recipient = userRepo.findByUsername(recipientUsername)
                .orElseThrow(() -> new UserException("Recipient not found", HttpStatus.NOT_FOUND));

        // Create and populate Message entity
        Message message = new Message();
        message.setSenderId(sender);
        message.setReceiverId(recipient);
        message.setMessageText(messageText);

        // Save and return
        return messageRepository.save(message);
    }



    public List<MessageDTO> getChat(String senderUsername, String recipientUsername) {
        User sender = userRepo.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User recipient = userRepo.findByUsername(recipientUsername)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        List<Message> messages = messageRepository.findBySenderIdAndReceiverId(sender, recipient);

        // Convert List<Message> to List<MessageDTO>
        return messages.stream()
                .map(message -> new MessageDTO(
                        message.getSenderId().getUsername(),
                        message.getReceiverId().getUsername(),
                        message.getMessageText()
                ))
                .collect(Collectors.toList());
    }


    public List<String> getConversations(String username) {
        return messageRepository.findConversationPartners(username);
    }


    public MessageDTO buildMessageDTO(Message message) {
        return MessageDTO.builder()
                .senderUsername(message.getSenderId().getUsername())
                .recipientUsername(message.getReceiverId().getUsername())
                .messageText(message.getMessageText())
                .timestamp(message.getSentAt())
                .build();
    }

}
