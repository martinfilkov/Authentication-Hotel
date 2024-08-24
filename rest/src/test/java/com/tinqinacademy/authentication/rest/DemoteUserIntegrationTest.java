package com.tinqinacademy.authentication.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserInput;
import com.tinqinacademy.authentication.core.services.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.authentication.rest.context.LoggedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DemoteUserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void createUser() {
        String password = passwordEncoder.encode("testtest");
        User user = User.builder()
                .email("test@test.com")
                .firstName("Test")
                .lastName("Testov")
                .phoneNumber("0000000000")
                .username("test")
                .password(password)
                .birthDate(LocalDate.now().minusYears(20))
                .roleType(RoleType.ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        loggedUser.setLoggedUser(user);
        Map<String, String> tokenCred = Map.of(
                "user_id", savedUser.getId().toString(),
                "role", savedUser.getRoleType().toString());
        loggedUser.setToken("Bearer " + jwtService.generateToken(tokenCred));
    }

    @AfterEach
    public void cleanUser() {
        this.userRepository.deleteAll();
    }

    @Test
    public void testDemoteUser_success() throws Exception {
        String password = passwordEncoder.encode("testtest");
        User user = User.builder()
                .email("test@test.com")
                .firstName("Test")
                .lastName("Testov")
                .phoneNumber("0000000000")
                .username("test")
                .password(password)
                .birthDate(LocalDate.now().minusYears(20))
                .roleType(RoleType.ADMIN)
                .build();
        User savedUser = userRepository.save(user);

        DemoteUserInput demoteInput = DemoteUserInput.builder()
                .userId(savedUser.getId().toString())
                .build();

        String input = objectMapper.writeValueAsString(demoteInput);

        mockMvc.perform(post(AuthenticationMappings.DEMOTE_USER)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, loggedUser.getToken()))
                .andExpect(status().isCreated());
    }

    @Test
    public void testDemoteUser_user_not_found_failure() throws Exception {
        DemoteUserInput demoteInput = DemoteUserInput.builder()
                .userId(UUID.randomUUID().toString())
                .build();

        String input = objectMapper.writeValueAsString(demoteInput);

        mockMvc.perform(post(AuthenticationMappings.DEMOTE_USER)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, loggedUser.getToken()))
                .andExpect(status().isNotFound());
    }
}
