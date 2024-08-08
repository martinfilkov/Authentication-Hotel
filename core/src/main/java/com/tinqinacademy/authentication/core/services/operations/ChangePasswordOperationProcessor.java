package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordOperation;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordOutput;
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
public class ChangePasswordOperationProcessor extends BaseOperationProcessor implements ChangePasswordOperation {
    private UserRepository userRepository;

    public ChangePasswordOperationProcessor(ConversionService conversionService,
                                            Validator validator,
                                            ErrorMapper errorMapper,
                                            UserRepository userRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, ChangePasswordOutput> process(ChangePasswordInput input) {
        return validateInput(input)
                .flatMap(validated -> changePassword(input));
    }

    private Either<Errors, ChangePasswordOutput> changePassword(ChangePasswordInput input) {
        return null;
    }
}
