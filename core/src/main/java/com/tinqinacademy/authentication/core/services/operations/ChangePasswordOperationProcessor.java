package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.InvalidInputException;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordOperation;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class ChangePasswordOperationProcessor extends BaseOperationProcessor implements ChangePasswordOperation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordOperationProcessor(ConversionService conversionService,
                                            Validator validator,
                                            ErrorMapper errorMapper,
                                            UserRepository userRepository,
                                            PasswordEncoder passwordEncoder) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<Errors, ChangePasswordOutput> process(ChangePasswordInput input) {
        return validateInput(input)
                .flatMap(validated -> changePassword(input));
    }

    private Either<Errors, ChangePasswordOutput> changePassword(ChangePasswordInput input) {
        return Try.of(() -> {
                    log.info("Start changePassword with input: {}", input);
                    User loggedUser = getLoggedUserIfValid(input);

                    checkIfPasswordMatches(input, loggedUser);
                    checkIfEmailMatches(input, loggedUser);
                    checkIfNewPasswordIsTheSameAsOldOne(input);

                    String encodedNewPassword = passwordEncoder.encode(input.getNewPassword());

                    User user = getUserByEmailIfExists(input);
                    user.setPassword(encodedNewPassword);
                    userRepository.save(user);

                    ChangePasswordOutput output = ChangePasswordOutput.builder().build();

                    log.info("End changePassword with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(InvalidInputException.class)), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private User getLoggedUserIfValid(ChangePasswordInput input) {
        Optional<User> userOptional = userRepository.findById(UUID.fromString(input.getUserId()));
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Logged user not found");
        }
        return userOptional.get();
    }

    private void checkIfPasswordMatches(ChangePasswordInput input, User loggedUser) {
        if (!passwordEncoder.matches(input.getOldPassword(), loggedUser.getPassword())) {
            throw new InvalidInputException("Current password does not match");
        }
    }

    private void checkIfEmailMatches(ChangePasswordInput input, User loggedUser) {
        if (!loggedUser.getEmail().equals(input.getEmail())) {
            throw new InvalidInputException("Email provided does not match");
        }
    }

    private void checkIfNewPasswordIsTheSameAsOldOne(ChangePasswordInput input) {
        if (input.getOldPassword().equals(input.getNewPassword())) {
            throw new InvalidInputException("New password cannot be the same as the old one");
        }
    }

    private User getUserByEmailIfExists(ChangePasswordInput input) {
        Optional<User> userOptional = userRepository.findByEmail(input.getEmail());

        if (userOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with email %s not found", input.getEmail()));
        }
        return userOptional.get();
    }
}
