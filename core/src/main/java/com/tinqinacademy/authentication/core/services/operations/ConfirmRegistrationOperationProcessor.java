package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationOperation;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
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
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class ConfirmRegistrationOperationProcessor extends BaseOperationProcessor implements ConfirmRegistrationOperation {
    private final UserRepository userRepository;
    private final RegistrationCodeRepository registrationCodeRepository;

    public ConfirmRegistrationOperationProcessor(ConversionService conversionService,
                                                 Validator validator,
                                                 ErrorMapper errorMapper,
                                                 UserRepository userRepository,
                                                 RegistrationCodeRepository registrationCodeRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
        this.registrationCodeRepository = registrationCodeRepository;
    }

    @Override
    public Either<Errors, ConfirmRegistrationOutput> process(ConfirmRegistrationInput input) {
        return validateInput(input)
                .flatMap(validated -> confirmRegistration(input));
    }

    private Either<Errors, ConfirmRegistrationOutput> confirmRegistration(ConfirmRegistrationInput input) {
        return Try.of(() -> {
                    log.info("Start confirmUser with input: {}", input);
                    RegistrationCode code = getRegistrationCodeIfExists(input);
                    registrationCodeRepository.delete(code);

                    ConfirmRegistrationOutput output = ConfirmRegistrationOutput.builder().build();
                    log.info("End confirmUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private RegistrationCode getRegistrationCodeIfExists(ConfirmRegistrationInput input){
        Optional<RegistrationCode> codeOptional = registrationCodeRepository.findByCode(input.getConfirmationCode());

        if (codeOptional.isEmpty()) {
            throw new NotFoundException("Confirmation code does not exist");
        }
        return codeOptional.get();
    }
}
