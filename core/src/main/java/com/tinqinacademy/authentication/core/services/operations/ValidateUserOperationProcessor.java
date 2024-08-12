package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.services.security.JwtService;
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
public class ValidateUserOperationProcessor extends BaseOperationProcessor implements ValidateUserOperation {
    private final JwtService jwtService;

    public ValidateUserOperationProcessor(ConversionService conversionService,
                                          Validator validator,
                                          ErrorMapper errorMapper,
                                          JwtService jwtService) {
        super(conversionService, validator, errorMapper);
        this.jwtService = jwtService;
    }

    @Override
    public Either<Errors, ValidateUserOutput> process(ValidateUserInput input) {
        return validateInput(input)
                .flatMap(validated -> validateUser(input));
    }

    private Either<Errors, ValidateUserOutput> validateUser(ValidateUserInput input) {
        return Try.of(() -> {
                    log.info("Start validateUser with input: {}", input);
                    Boolean isValid = jwtService.isTokenValid(input.getToken());

                    ValidateUserOutput output = ValidateUserOutput.builder()
                            .validity(isValid)
                            .build();

                    log.info("End validateUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST));
    }
}
