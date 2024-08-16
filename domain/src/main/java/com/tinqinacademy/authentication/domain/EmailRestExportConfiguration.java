package com.tinqinacademy.authentication.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.email.restexport.EmailRestClient;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class EmailRestExportConfiguration {
    private final ObjectMapper objectMapper;

    @Value(value = "${email.service.url}")
    private String emailUrl;

    @Bean(name = "EmailRestClient")
    public EmailRestClient emailRestClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(EmailRestClient.class, emailUrl);
    }
}
