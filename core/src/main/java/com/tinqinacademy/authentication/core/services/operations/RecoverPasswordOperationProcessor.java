package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordOperation;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordOutput;
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
public class RecoverPasswordOperationProcessor extends BaseOperationProcessor implements RecoverPasswordOperation {
    private final UserRepository userRepository;

    public RecoverPasswordOperationProcessor(ConversionService conversionService,
                                             Validator validator,
                                             ErrorMapper errorMapper,
                                             UserRepository userRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, RecoverPasswordOutput> process(RecoverPasswordInput input) {
        return validateInput(input)
                .flatMap(validated -> recoverPassword(input));
    }

    private Either<Errors, RecoverPasswordOutput> recoverPassword(RecoverPasswordInput input) {
        return null;
    }
}
