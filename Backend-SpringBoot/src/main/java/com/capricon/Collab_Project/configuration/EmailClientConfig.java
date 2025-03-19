package com.capricon.Collab_Project.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class EmailClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds connection timeout
        factory.setReadTimeout(10000); //10 seconds read timeout

        return templateBuilder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(factory))
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("accept", "application/json")
                .build();
    }

}
