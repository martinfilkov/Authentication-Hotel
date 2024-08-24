package com.tinqinacademy.authentication.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.core.services.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.RegistrationCodeRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class LoginUserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void testLoginUser_success() throws Exception {
        LoginUserInput loginInput = LoginUserInput.builder()
                .username("test")
                .password("testtest")
                .build();

        String input = objectMapper.writeValueAsString(loginInput);

        mockMvc.perform(post(AuthenticationMappings.LOGIN_USER)
                .content(input)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testLoginUser_name_not_matching_failure() throws Exception {
        LoginUserInput loginInput = LoginUserInput.builder()
                .username("test123")
                .password("testtest")
                .build();

        String input = objectMapper.writeValueAsString(loginInput);

        mockMvc.perform(post(AuthenticationMappings.LOGIN_USER)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.errors[0].message")
                        .value(String.format("User with username %s does not exist", loginInput.getUsername())));
    }
}
