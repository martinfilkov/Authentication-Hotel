package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
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
public class LoginUserOperationProcessor extends BaseOperationProcessor implements LoginUserOperation {
    private final UserRepository userRepository;

    public LoginUserOperationProcessor(ConversionService conversionService,
                                       Validator validator,
                                       ErrorMapper errorMapper,
                                       UserRepository userRepository) {
        super(conversionService, validator, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, LoginUserOutput> process(LoginUserInput input) {
        return validateInput(input)
                .flatMap(validated -> loginUser(input));
    }

    private Either<Errors, LoginUserOutput> loginUser(LoginUserInput input) {
        return null;
    }
}
