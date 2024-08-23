package com.tinqinacademy.authentication.rest;

import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.RegistrationCodeRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.email.api.operations.email.confirm.ConfirmEmailInput;
import com.tinqinacademy.email.api.operations.email.confirm.ConfirmEmailOutput;
import com.tinqinacademy.email.restexport.EmailRestClient;
import org.junit.jupiter.api.AfterEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RegisterUserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RegistrationCodeRepository registrationCodeRepository;

    @MockBean
    private EmailRestClient emailRestClient;

    @AfterEach
    public void cleanUserAndCode() {
        this.userRepository.deleteAll();
        this.registrationCodeRepository.deleteAll();
    }

    @Test
    public void testRegisterUser_success() throws Exception {
        String input = """
                {
                  "username": "test",
                  "password": "testtest",
                  "firstName": "Test",
                  "lastName": "Testov",
                  "email": "test@test.test",
                  "birthDate": "1999-08-23",
                  "phoneNumber": "0000000000"
                }
                """;

        String password = passwordEncoder.encode("test");
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

        when(userRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.empty());

        when(emailRestClient.confirmEmail(any(ConfirmEmailInput.class)))
                .thenReturn(new ConfirmEmailOutput());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        mockMvc.perform(post(AuthenticationMappings.REGISTER_USER)
                .content(input)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
