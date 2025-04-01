package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.components.RabbitMQProperties;
import com.capricon.Collab_Project.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitMQProperties properties;

    @InjectMocks
    private RabbitMQPublisher mqPublisher;

    @BeforeEach
    void setUp() {
        Map<String, String> exchanges = Map.of("user", "user.exchange");
        when(properties.getExchanges()).thenReturn(exchanges);
    }

    @Test
    void testPublishUserSignUpEvent() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName("John Doe");
        userDTO.setEmail("johndoe@example.com");
        userDTO.setUsername("@johndoe");

        mqPublisher.sendMessage("user", "user.signup", userDTO);

        verify(rabbitTemplate, times(1)).convertAndSend("user.exchange", "user.signup.key", userDTO);

    }

}
