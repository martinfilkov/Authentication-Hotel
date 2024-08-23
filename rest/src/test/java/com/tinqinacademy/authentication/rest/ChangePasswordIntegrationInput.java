package com.tinqinacademy.authentication.rest;

import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.core.services.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.authentication.rest.context.LoggedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ChangePasswordIntegrationInput {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoggedUser loggedUser;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    private User user;

    @BeforeEach
    public void setUser() {
        String password = passwordEncoder.encode("testtest");
        user = User.builder()
                .id(UUID.fromString("179b898d-a8df-491e-aaa6-34116cfb1189"))
                .email("test@test.com")
                .firstName("Test")
                .lastName("Testov")
                .phoneNumber("0000000000")
                .username("test")
                .password(password)
                .birthDate(LocalDate.now().minusYears(20))
                .roleType(RoleType.ADMIN)
                .build();
        loggedUser.setLoggedUser(user);
        loggedUser.setToken("Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMTc5Yjg5OGQtYThkZi00OTFlLWFhYTYtMzQxMTZjZmIxMTg5Iiwicm9sZSI6ImFkbWluIiwiaWF0IjoxNzI0NDAxMDk1LCJleHAiOjU3MjQ0MDQwOTV9.zR4tAWKYR8eofeSLf4fMaKMeTQdSW2K0WiUzKBoHwj8");
    }

    @Test
    public void testChangePassword_success() throws Exception {
        String input = """
                {
                  "oldPassword": "testtest",
                  "newPassword": "stringst",
                  "email": "test@test.com"
                }
                """;

        when(jwtService.isTokenValid(any(String.class)))
                .thenReturn(true);

        when(jwtService.extractUserId(any(String.class)))
                .thenReturn(loggedUser.getLoggedUser().getId().toString());

        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post(AuthenticationMappings.CHANGE_PASSWORD)
                .content(input)
                .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, loggedUser.getToken()))
                .andExpect(status().isCreated());
    }
}
