package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.services.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class LoginUserOperationProcessor extends BaseOperationProcessor implements LoginUserOperation {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginUserOperationProcessor(ConversionService conversionService,
                                       Validator validator,
                                       ErrorMapper errorMapper,
                                       UserRepository userRepository,
                                       AuthenticationManager authenticationManager, JwtService jwtService) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public Either<Errors, LoginUserOutput> process(LoginUserInput input) {
        return validateInput(input)
                .flatMap(validated -> loginUser(input));
    }

    private Either<Errors, LoginUserOutput> loginUser(LoginUserInput input) {
        return Try.of(() -> {
                    log.info("Start loginUser with input: {}", input);
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    input.getUsername(),
                                    input.getPassword()
                            )
                    );

                    User user = getUserWithIfUsernameExists(input);

                    String jwtToken = jwtService.generateToken(user);

                    LoginUserOutput output = LoginUserOutput.builder()
                            .token(jwtToken)
                            .build();

                    log.info("End loginUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private User getUserWithIfUsernameExists(LoginUserInput input){
        Optional<User> userWithUsername = userRepository.findByUsername(input.getUsername());
        if (userWithUsername.isEmpty())
            throw new NotFoundException(String.format("User with username %s does not exist", input.getUsername()));
        return userWithUsername.get();
    }
}
