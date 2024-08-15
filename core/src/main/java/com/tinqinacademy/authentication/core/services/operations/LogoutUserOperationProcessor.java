package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.logout.LogoutUserInput;
import com.tinqinacademy.authentication.api.operations.operations.logout.LogoutUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.logout.LogoutUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.entities.BlacklistToken;
import com.tinqinacademy.authentication.persistence.repositories.BlackListTokenRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class LogoutUserOperationProcessor extends BaseOperationProcessor implements LogoutUserOperation {
    private final BlackListTokenRepository blackListTokenRepository;

    public LogoutUserOperationProcessor(ConversionService conversionService,
                                        Validator validator,
                                        ErrorMapper errorMapper,
                                        BlackListTokenRepository blackListTokenRepository) {
        super(conversionService, validator, errorMapper);
        this.blackListTokenRepository = blackListTokenRepository;
    }

    @Override
    public Either<Errors, LogoutUserOutput> process(LogoutUserInput input) {
        return validateInput(input)
                .flatMap(validated -> logoutUser(input));
    }

    private Either<Errors, LogoutUserOutput> logoutUser(LogoutUserInput input) {
        return Try.of(() -> {
                    log.info("Start logoutUser with input: {}", input);

                    checkIfTokenIsAlreadyBlacklisted(input);

                    BlacklistToken blacklistToken = conversionService.convert(input, BlacklistToken.class);
                    blackListTokenRepository.save(blacklistToken);

                    LogoutUserOutput output = LogoutUserOutput.builder().build();
                    log.info("End logoutUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotAvailableException.class)), ex -> errorMapper.handleError(ex, HttpStatus.CONFLICT)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private void checkIfTokenIsAlreadyBlacklisted(LogoutUserInput input){
        Optional<BlacklistToken> token = blackListTokenRepository.findByToken(input.getToken());
        if (token.isPresent()){
            throw new NotAvailableException("User has already logged out");
        }
    }
}
