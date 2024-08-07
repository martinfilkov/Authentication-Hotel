package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserInput;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserOutput;
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
public class PromoteOperationProcessor extends BaseOperationProcessor implements PromoteUserOperation {
    private final UserRepository userRepository;

    public PromoteOperationProcessor(ConversionService conversionService,
                                     Validator validator,
                                     ErrorMapper errorMapper,
                                     UserRepository userRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, PromoteUserOutput> process(PromoteUserInput input) {
        return validateInput(input)
                .flatMap(validated -> promoteUser(input));
    }

    private Either<Errors, PromoteUserOutput> promoteUser(PromoteUserInput input) {
        return null;
    }
}
