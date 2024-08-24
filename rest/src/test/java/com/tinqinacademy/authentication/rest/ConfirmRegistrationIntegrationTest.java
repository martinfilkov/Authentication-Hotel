package com.tinqinacademy.authentication.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.persistence.entities.RegistrationCode;
import com.tinqinacademy.authentication.persistence.repositories.RegistrationCodeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ConfirmRegistrationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrationCodeRepository registrationCodeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void createRegistrationToken() {
        RegistrationCode code = RegistrationCode.builder()
                .code("stringstring")
                .email("test@test.com")
                .build();
        this.registrationCodeRepository.save(code);
    }

    @AfterEach
    public void cleanCodes() {
        this.registrationCodeRepository.deleteAll();
    }

    @Test
    public void testConfirmRegistration_success() throws Exception {
        ConfirmRegistrationInput confirmInput = ConfirmRegistrationInput.builder()
                .confirmationCode("stringstring")
                .build();

        String input = objectMapper.writeValueAsString(confirmInput);

        mockMvc.perform(post(AuthenticationMappings.CONFIRM_REGISTRATION)
                .content(input)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testConfirmRegistration_code_not_found_failure() throws Exception {
        ConfirmRegistrationInput confirmInput = ConfirmRegistrationInput.builder()
                .confirmationCode("stringmelons")
                .build();

        String input = objectMapper.writeValueAsString(confirmInput);

        mockMvc.perform(post(AuthenticationMappings.CONFIRM_REGISTRATION)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
