package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserInput;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserOutput;
import com.tinqinacademy.authentication.core.ErrorMapper;
import com.tinqinacademy.authentication.core.services.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
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
        return Try.of(() -> {
                    log.info("Start promoteUser with input: {}", input);
                    User user = getUserIfExists(input);

                    checkIfUserAlreadyAdmin(user);

                    user.setRoleType(RoleType.ADMIN);
                    userRepository.save(user);

                    PromoteUserOutput output = PromoteUserOutput.builder().build();
                    log.info("End promoteUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(instanceOf(NotAvailableException.class)), ex -> errorMapper.handleError(ex, HttpStatus.CONFLICT)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private User getUserIfExists(PromoteUserInput input) {
        Optional<User> userOptional = userRepository.findById(UUID.fromString(input.getUserId()));
        if (userOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id %s not found", input.getUserId()));
        }
        return userOptional.get();
    }

    private void checkIfUserAlreadyAdmin(User user) {
        if (user.getRoleType() == RoleType.ADMIN) {
            throw new NotAvailableException("User already admin");
        }
    }
}
