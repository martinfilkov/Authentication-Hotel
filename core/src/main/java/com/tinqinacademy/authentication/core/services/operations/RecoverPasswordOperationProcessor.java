package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordOperation;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.LoggedUser;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class RecoverPasswordOperationProcessor extends BaseOperationProcessor implements RecoverPasswordOperation {
    private final UserRepository userRepository;
    private final LoggedUser loggedUser;
    private final PasswordEncoder passwordEncoder;

    public RecoverPasswordOperationProcessor(ConversionService conversionService,
                                             Validator validator,
                                             ErrorMapper errorMapper,
                                             UserRepository userRepository,
                                             LoggedUser loggedUser,
                                             PasswordEncoder passwordEncoder) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
        this.loggedUser = loggedUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<Errors, RecoverPasswordOutput> process(RecoverPasswordInput input) {
        return validateInput(input)
                .flatMap(validated -> recoverPassword(input));
    }

    private Either<Errors, RecoverPasswordOutput> recoverPassword(RecoverPasswordInput input) {
        return Try.of(() -> {
                    log.info("Start recoverPassword with input: {}", input);
                    User user = getUserByEmailIfExists(input);

                    String generatedPassword = passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(8));
                    user.setPassword(generatedPassword);
                    userRepository.save(user);

                    RecoverPasswordOutput output = RecoverPasswordOutput.builder().build();
                    log.info("End recoverPassword with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotAvailableException.class)), ex -> errorMapper.handleError(ex, HttpStatus.CONFLICT)),
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private User getUserByEmailIfExists(RecoverPasswordInput input) {
        Optional<User> userOptional = userRepository.findByEmail(input.getEmail());

        if (userOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with email %s not found", input.getEmail()));
        }
        return userOptional.get();
    }
}
