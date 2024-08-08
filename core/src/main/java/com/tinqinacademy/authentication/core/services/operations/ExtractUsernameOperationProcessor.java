package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.extract.ExtractUserInput;
import com.tinqinacademy.authentication.api.operations.operations.extract.ExtractUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.extract.ExtractUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.services.JwtService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.*;

@Slf4j
@Service
public class ExtractUsernameOperationProcessor extends BaseOperationProcessor implements ExtractUserOperation {
    private final JwtService jwtService;

    public ExtractUsernameOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper, JwtService jwtService) {
        super(conversionService, validator, errorMapper);
        this.jwtService = jwtService;
    }

    @Override
    public Either<Errors, ExtractUserOutput> process(ExtractUserInput input) {
        return validateInput(input)
                .flatMap(validated -> extractUsername(input));
    }

    private Either<Errors, ExtractUserOutput> extractUsername(ExtractUserInput input) {
        return Try.of(() -> {
                    log.info("Start extractUsername with input: {}", input);
                    String username = jwtService.extractUsername(input.getToken());

                    ExtractUserOutput output = ExtractUserOutput.builder()
                            .username(username)
                            .build();

                    log.info("End extractUsername with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }
}
