package com.tinqinacademy.authentication.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.RegistrationCodeRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.email.api.operations.email.confirm.ConfirmEmailInput;
import com.tinqinacademy.email.api.operations.email.confirm.ConfirmEmailOutput;
import com.tinqinacademy.email.restexport.EmailRestClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationCodeRepository registrationCodeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailRestClient emailRestClient;

    @AfterEach
    public void cleanUserAndCode() {
        this.userRepository.deleteAll();
        this.registrationCodeRepository.deleteAll();
    }

    @Test
    public void testRegisterUser_success() throws Exception {
        String username = "test";
        String password = "testtest";
        String firstName = "Test";
        String lastName = "Testov";
        String email = "test@test.test";
        LocalDate birthDate = LocalDate.now().minusYears(20);
        String phoneNumber = "0000000000";
        RegisterUserInput registerInput = RegisterUserInput.builder()
                .username(username)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .build();

        String input = objectMapper.writeValueAsString(registerInput);

        MvcResult mvcResult = mockMvc.perform(post(AuthenticationMappings.REGISTER_USER)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        User user = userRepository.findAll().getFirst();
        Assertions.assertEquals(registerInput.getEmail(), user.getEmail());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"", "test", "123123123"})
    public void return_400_when_email_invalid(String email) {
        String username = "test";
        String password = "testtest";
        String firstName = "Test";
        String lastName = "Testov";
        LocalDate birthDate = LocalDate.now().minusYears(20);
        String phoneNumber = "0000000000";
        RegisterUserInput registerInput = RegisterUserInput.builder()
                .username(username)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .build();

        String input = objectMapper.writeValueAsString(registerInput);

         mockMvc.perform(post(AuthenticationMappings.REGISTER_USER)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
