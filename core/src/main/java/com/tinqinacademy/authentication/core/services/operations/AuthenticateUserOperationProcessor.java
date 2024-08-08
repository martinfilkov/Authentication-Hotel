package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.services.JwtService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static io.vavr.API.*;

@Slf4j
@Service
public class AuthenticateUserOperationProcessor extends BaseOperationProcessor implements AuthenticateUserOperation {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthenticateUserOperationProcessor(ConversionService conversionService,
                                              Validator validator,
                                              ErrorMapper errorMapper,
                                              JwtService jwtService,
                                              UserDetailsService userDetailsService) {
        super(conversionService, validator, errorMapper);
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Either<Errors, AuthenticateUserOutput> process(AuthenticateUserInput input) {
        return validateInput(input)
                .flatMap(validated -> authenticateUser(input));
    }

    private Either<Errors, AuthenticateUserOutput> authenticateUser(AuthenticateUserInput input) {
        return Try.of(() -> {
                    log.info("Start authenticateUser with input: {}", input);
                    String username = jwtService.extractUsername(input.getToken());

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    Boolean isValid = jwtService.isTokenValid(input.getToken(), username);

                    AuthenticateUserOutput output = AuthenticateUserOutput.builder()
                            .userDetails(userDetails)
                            .build();

                    log.info("End authenticateUser with output: {}", output);
                    return isValid ? output : null;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }
}
