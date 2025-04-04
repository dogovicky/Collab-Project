package com.capricon.Collab_Project.components;

import com.capricon.Collab_Project.dto.SignUpRequest;
import com.capricon.Collab_Project.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQConsumer {

    //@RabbitListener(queues = "${spring.rabbitmq.queues.user}")
    public void consumeMessage(UserDTO userDTO) {
        log.info("Received x User: {}", userDTO);
    }

    //@RabbitListener(queues = "${spring.rabbitmq.queues.user}")
    public void consumeMessage(SignUpRequest signUpRequest) {
        log.info("Received User: {}", signUpRequest);
    }

}
