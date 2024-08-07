package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationOperation;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConfirmRegistrationOperationProcessor extends BaseOperationProcessor implements ConfirmRegistrationOperation {
    private final UserRepository userRepository;

    public ConfirmRegistrationOperationProcessor(ConversionService conversionService,
                                                 Validator validator,
                                                 ErrorMapper errorMapper,
                                                 UserRepository userRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, ConfirmRegistrationOutput> process(ConfirmRegistrationInput input) {
        return validateInput(input)
                .flatMap(validated -> confirmRegistration(input));
    }

    private Either<Errors, ConfirmRegistrationOutput> confirmRegistration(ConfirmRegistrationInput input) {
        return null;
    }
}
