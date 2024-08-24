package com.tinqinacademy.authentication.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordInput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.email.restexport.EmailRestClient;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RecoverPasswordIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailRestClient emailRestClient;

    @BeforeEach
    public void createUser() {
        String password = passwordEncoder.encode("testtest");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .firstName("Test")
                .lastName("Testov")
                .phoneNumber("0000000000")
                .username("test")
                .password(password)
                .birthDate(LocalDate.now().minusYears(20))
                .roleType(RoleType.ADMIN)
                .build();
        userRepository.save(user);
    }

    @AfterEach
    public void cleanUser() {
        this.userRepository.deleteAll();
    }

    @Test
    public void testRecoverPassword_success() throws Exception {
        RecoverPasswordInput recoverInput = RecoverPasswordInput.builder()
                .email("test@test.com")
                .build();

        String input = objectMapper.writeValueAsString(recoverInput);

        mockMvc.perform(post(AuthenticationMappings.RECOVER_PASSWORD)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testRecoverPassword_user_not_found_failure() throws Exception {
        RecoverPasswordInput recoverInput = RecoverPasswordInput.builder()
                .email("test123@test.com")
                .build();

        String input = objectMapper.writeValueAsString(recoverInput);

        mockMvc.perform(post(AuthenticationMappings.RECOVER_PASSWORD)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.errors[0].message")
                        .value(String.format("User with email %s not found", recoverInput.getEmail())));;
    }
}
