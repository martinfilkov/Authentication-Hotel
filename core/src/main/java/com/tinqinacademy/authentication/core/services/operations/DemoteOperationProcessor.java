package com.tinqinacademy.authentication.core.services.operations;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserInput;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserOperation;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserInput;
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
                .flatMap(validated -> demoteUser(input));
    }

    private Either<Errors, DemoteUserOutput> demoteUser(DemoteUserInput input) {
        return Try.of(() -> {
                    log.info("Start demoteUser with input: {}", input);
                    User user = getUserIfExists(input);
                    user.setRoleType(RoleType.USER);
                    userRepository.save(user);

                    DemoteUserOutput output = DemoteUserOutput.builder().build();
                    log.info("End demoteUser with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private User getUserIfExists(DemoteUserInput input) {
        Optional<User> userOptional = userRepository.findById(UUID.fromString(input.getUserId()));
        if (userOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id %s not found", input.getUserId()));
        }
        return userOptional.get();
    }
}
