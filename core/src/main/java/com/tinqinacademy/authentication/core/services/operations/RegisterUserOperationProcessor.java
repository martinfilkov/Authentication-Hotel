package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static io.vavr.API.*;

@Slf4j
@Service
public class RegisterUserOperationProcessor extends BaseOperationProcessor implements RegisterUserOperation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserOperationProcessor(ConversionService conversionService,
                                          Validator validator,
                                          ErrorMapper errorMapper,
                                          UserRepository userRepository,
                                          PasswordEncoder passwordEncoder) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

                    User user = User.builder()
                            .username(input.getUsername())
                            .password(passwordEncoder.encode(input.getPassword()))
                            .email(input.getEmail())
                            .roleType(RoleType.USER)
                            .birthDate(input.getBirthDate())
                            .phoneNumber(input.getPhoneNumber())
                            .build();

                    User savedUser = userRepository.save(user);

                    RegisterUserOutput output = RegisterUserOutput.builder()
                            .id(savedUser.getId())
                            .build();

                    log.info("End registerUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private void checkIfUserWithUsernameExists(RegisterUserInput input){
        Optional<User> userWithUsername = userRepository.findByUsername(input.getUsername());
        if (userWithUsername.isPresent())
            throw new RuntimeException(String.format("User with username %s already exists", input.getUsername()));
    }

    private void checkIfUserWithEmailExists(RegisterUserInput input){
        Optional<User> userWithEmail = userRepository.findByEmail(input.getEmail());
        if (userWithEmail.isPresent())
            throw new RuntimeException(String.format("User with email %s already exists", input.getEmail()));
    }
}
