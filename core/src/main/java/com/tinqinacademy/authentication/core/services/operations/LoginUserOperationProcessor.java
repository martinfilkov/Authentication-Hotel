package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.services.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.RegistrationCode;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.RegistrationCodeRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class LoginUserOperationProcessor extends BaseOperationProcessor implements LoginUserOperation {
    private final UserRepository userRepository;
    private final RegistrationCodeRepository registrationCodeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginUserOperationProcessor(ConversionService conversionService,
                                       Validator validator,
                                       ErrorMapper errorMapper,
                                       UserRepository userRepository,
                                       RegistrationCodeRepository registrationCodeRepository,
                                       JwtService jwtService,
                                       PasswordEncoder passwordEncoder) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
        this.registrationCodeRepository = registrationCodeRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<Errors, LoginUserOutput> process(LoginUserInput input) {
        return validateInput(input)
                .flatMap(validated -> loginUser(input));
    }

    private Either<Errors, LoginUserOutput> loginUser(LoginUserInput input) {
        return Try.of(() -> {
                    log.info("Start loginUser with input: {}", input);
                    User user = getUserWithIfUsernameExists(input);

                    checkIfUserCredentialsMatch(input, user);
                    checkIfUserIsConfirmed(user);

                    String jwtToken = jwtService.generateToken(Map.of(
                            "user_id", user.getId().toString(),
                            "role", user.getRoleType().toString()));

                    LoginUserOutput output = LoginUserOutput.builder()
                            .token(jwtToken)
                            .build();

                    log.info("End loginUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(instanceOf(NotAvailableException.class)), ex -> errorMapper.handleError(ex, HttpStatus.CONFLICT)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }


    private void checkIfUserCredentialsMatch(LoginUserInput input, User user) {
        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new NotFoundException("User password is not matching");
        }
    }

    private void checkIfUserIsConfirmed(User user){
        Optional<RegistrationCode> registrationCode = registrationCodeRepository.findByEmail(user.getEmail());
        if (registrationCode.isPresent()) {
            throw new NotAvailableException(String.format("User with email %s is not confirmed", user.getEmail()));
        }
    }

    private User getUserWithIfUsernameExists(LoginUserInput input) {
        Optional<User> userWithUsername = userRepository.findByUsername(input.getUsername());
        if (userWithUsername.isEmpty())
            throw new NotFoundException(String.format("User with username %s does not exist", input.getUsername()));
        return userWithUsername.get();
    }
}
