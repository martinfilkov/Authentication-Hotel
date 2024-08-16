package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.entities.RegistrationCode;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.RegistrationCodeRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.email.api.operations.email.confirm.ConfirmEmailInput;
import com.tinqinacademy.email.restexport.EmailRestClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class RegisterUserOperationProcessor extends BaseOperationProcessor implements RegisterUserOperation {
    private final UserRepository userRepository;
    private final RegistrationCodeRepository registrationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailRestClient emailRestClient;

    public RegisterUserOperationProcessor(ConversionService conversionService,
                                          Validator validator,
                                          ErrorMapper errorMapper,
                                          UserRepository userRepository,
                                          RegistrationCodeRepository registrationCodeRepository,
                                          PasswordEncoder passwordEncoder,
                                          EmailRestClient emailRestClient) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
        this.registrationCodeRepository = registrationCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailRestClient = emailRestClient;
    }

    @Override
    public Either<Errors, RegisterUserOutput> process(RegisterUserInput input) {
        return validateInput(input)
                .flatMap(validated -> registerUser(input));
    }

    private Either<Errors, RegisterUserOutput> registerUser(RegisterUserInput input) {
        return Try.of(() -> {
                    log.info("Start registerUser with input: {}", input);

                    checkIfUserWithUsernameExists(input);
                    checkIfUserWithEmailExists(input);
                    checkIfUserIsAdult(input);

                    User user = conversionService.convert(input, User.UserBuilder.class)
                            .password(passwordEncoder.encode(input.getPassword()))
                            .build();

                    User savedUser = userRepository.save(user);

                    String confirmCode = RandomStringUtils.randomAlphanumeric(12);
                    RegistrationCode registrationCode = RegistrationCode.builder()
                            .code(confirmCode)
                            .email(savedUser.getEmail())
                            .build();

                    registrationCodeRepository.save(registrationCode);
                    sendConfirmEmail(registrationCode);

                    RegisterUserOutput output = conversionService.convert(savedUser, RegisterUserOutput.class);

                    log.info("End registerUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotAvailableException.class)), ex -> errorMapper.handleError(ex, HttpStatus.CONFLICT)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private void sendConfirmEmail(RegistrationCode registrationCode) {
        ConfirmEmailInput input = conversionService.convert(registrationCode, ConfirmEmailInput.class);
        emailRestClient.confirmEmail(input);
    }

    private void checkIfUserWithUsernameExists(RegisterUserInput input) {
        Optional<User> userWithUsername = userRepository.findByUsername(input.getUsername());
        if (userWithUsername.isPresent()) {
            throw new NotAvailableException(String.format("User with username %s already exists", input.getUsername()));
        }
    }

    private void checkIfUserWithEmailExists(RegisterUserInput input) {
        Optional<User> userWithEmail = userRepository.findByEmail(input.getEmail());
        if (userWithEmail.isPresent()) {
            throw new NotAvailableException(String.format("User with email %s already exists", input.getEmail()));
        }
    }

    private void checkIfUserIsAdult(RegisterUserInput input) {
        if (input.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new NotAvailableException("You need to be at least 18 to register");
        }
    }
}
