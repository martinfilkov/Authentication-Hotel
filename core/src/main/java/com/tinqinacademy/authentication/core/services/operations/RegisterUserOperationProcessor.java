package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
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
public class RegisterUserOperationProcessor extends BaseOperationProcessor implements RegisterUserOperation {
    private final UserRepository userRepository;

    public RegisterUserOperationProcessor(ConversionService conversionService,
                                          Validator validator,
                                          ErrorMapper errorMapper,
                                          UserRepository userRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, RegisterUserOutput> process(RegisterUserInput input) {
        return validateInput(input)
                .flatMap(validated -> registerUser(input));
    }

    private Either<Errors, RegisterUserOutput> registerUser(RegisterUserInput input) {
        return null;
    }
}
