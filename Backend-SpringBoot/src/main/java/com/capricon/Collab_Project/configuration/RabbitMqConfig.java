package com.capricon.Collab_Project.configuration;

import com.capricon.Collab_Project.components.RabbitMQProperties;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    private final RabbitMQProperties properties;

    public RabbitMqConfig(RabbitMQProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUri(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        //Enable publisher confirms and returns for reliable messaging
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;

    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter()); // Use JSON
        return template;
    }

    // Exchanges, queues  and routing-keys configurations
    @Bean
    public Map<String, DirectExchange> exchanges() {
        return properties.getExchanges().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new DirectExchange(entry.getValue())));
    }

    @Bean
    public Map<String, Queue> queues() {
        return properties.getQueues().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new Queue(entry.getValue())));
    }

    @Bean
    public Declarables bindings() {
        return new Declarables(
                properties.getRoutingKeys().entrySet().stream()
                        .map(entry -> {
                            String routingKeyName = entry.getKey();
                            String routingKey = entry.getValue();
                            String queueName = properties.getQueues().get(routingKeyName.split("\\.")[0]);

                            String exchangeName = properties.getExchanges().entrySet()
                                    .stream()
                                    .filter(e -> routingKeyName.startsWith(e.getKey()))
                                    .map(Map.Entry::getValue)
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("No matching exchange for routing key: " + routingKeyName));

                            return BindingBuilder
                                    .bind(new Queue(queueName, true))
                                    .to(new DirectExchange(exchangeName))
                                    .with(routingKey);
                        })
                        .collect(Collectors.toList())
        );
    }

}
