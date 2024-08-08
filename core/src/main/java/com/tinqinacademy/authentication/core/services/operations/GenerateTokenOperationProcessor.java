package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenInput;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenOperation;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenOutput;
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
public class GenerateTokenOperationProcessor extends BaseOperationProcessor implements GenerateTokenOperation {
    private final UserDetailsService userDetailsService;

    public GenerateTokenOperationProcessor(ConversionService conversionService,
                                           Validator validator,
                                           ErrorMapper errorMapper,
                                           UserDetailsService userDetailsService) {
        super(conversionService, validator, errorMapper);
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Either<Errors, GenerateTokenOutput> process(GenerateTokenInput input) {
        return validateInput(input)
                .flatMap(validated -> generateToken(input));
    }

    private Either<Errors, GenerateTokenOutput> generateToken(GenerateTokenInput input) {
        return Try.of(() -> {
                    log.info("Start generateToken with input: {}", input);

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(input.getUsername());

                    GenerateTokenOutput output = GenerateTokenOutput.builder()
                            .userDetails(userDetails)
                            .build();

                    log.info("End generateToken with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }
}
