package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserInput;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserOutput;
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
public class DemoteOperationProcessor extends BaseOperationProcessor implements DemoteUserOperation {
    private final UserRepository userRepository;

    public DemoteOperationProcessor(ConversionService conversionService,
                                    Validator validator,
                                    ErrorMapper errorMapper,
                                    UserRepository userRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, DemoteUserOutput> process(DemoteUserInput input) {
        return validateInput(input)
                .flatMap(validated -> promoteUser(input));
    }

    private Either<Errors, DemoteUserOutput> promoteUser(DemoteUserInput input) {
        return null;
    }
}
