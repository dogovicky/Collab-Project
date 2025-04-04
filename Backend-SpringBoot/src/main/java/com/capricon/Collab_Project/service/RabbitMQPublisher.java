package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.components.RabbitMQProperties;
import com.capricon.Collab_Project.exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    /*
       * Publishes an event to RabbitMQ dynamically based on event type
       *
       *
       * @Param exchangeKey The key to identify exchange from application.yml
       * @Param routingKey The key to identify routing key from application.yml
       * @Param payload The actual event object to send

     */

    public void sendMessage(String exchangeKey, String routingKey, Object payload) {
        try {
            log.info("Message method called with exchange = {}, routing key = {}", exchangeKey, routingKey);
            String exchange = properties.getExchanges().get(exchangeKey);
            String routing = properties.getRoutingKeys().get(routingKey);

            log.info("Resolved exchange = {}, routing = {}", exchange, routing);
            log.info("RabbitMQ Host: {}", rabbitTemplate.getConnectionFactory().getHost());
            rabbitTemplate.convertAndSend(exchange, routing, payload);
            log.info("Message called and processed.");
        } catch (Exception ex) {
            log.error("An error occurred during message sending: {}", ex.getMessage());
            throw new TechnicalException("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
