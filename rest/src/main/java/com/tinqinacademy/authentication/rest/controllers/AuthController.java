package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.operations.change.ChangePasswordOutput;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.operations.confirm.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserInput;
import com.tinqinacademy.authentication.api.operations.operations.demote.DemoteUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserInput;
import com.tinqinacademy.authentication.api.operations.operations.promote.PromoteUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.operations.recover.RecoverPasswordOutput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserOutput;
import com.tinqinacademy.authentication.core.services.operations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final RegisterUserOperationProcessor registerUserOperationProcessor;
    private final LoginUserOperationProcessor loginUserOperationProcessor;
    private final RecoverPasswordOperationProcessor recoverPasswordOperationProcessor;
    private final ConfirmRegistrationOperationProcessor confirmRegistrationOperationProcessor;
    private final ChangePasswordOperationProcessor changePasswordOperationProcessor;
    private final PromoteOperationProcessor promoteOperationProcessor;
    private final DemoteOperationProcessor demoteOperationProcessor;
    private final ValidateUserOperationProcessor validateUserOperationProcessor;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully validated user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.REGISTER_USER)
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserInput input) {
        Either<Errors, RegisterUserOutput> output = registerUserOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.LOGIN_USER)
    public ResponseEntity<?> loginUser(@RequestBody LoginUserInput input) {
        Either<Errors, LoginUserOutput> output = loginUserOperationProcessor.process(input);
        if (output.isLeft()) {
            Errors errors = output.getLeft();
            return new ResponseEntity<>(errors, HttpStatusCode.valueOf(errors.getCode()));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + output.get().getToken());
        return new ResponseEntity<>(output.get(), headers, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.RECOVER_PASSWORD)
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordInput input) {
        Either<Errors, RecoverPasswordOutput> output = recoverPasswordOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.CONFIRM_REGISTRATION)
    public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationInput input) {
        Either<Errors, ConfirmRegistrationOutput> output = confirmRegistrationOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordInput input) {
        Either<Errors, ChangePasswordOutput> output = changePasswordOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.PROMOTE_USER)
    public ResponseEntity<?> promoteUser(@RequestBody PromoteUserInput input) {
        Either<Errors, PromoteUserOutput> output = promoteOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.DEMOTE_USER)
    public ResponseEntity<?> demoteUser(@RequestBody DemoteUserInput input) {
        Either<Errors, DemoteUserOutput> output = demoteOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully validated user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.VALIDATE_TOKEN)
    public ResponseEntity<?> validateUser(@RequestBody ValidateUserInput input) {
        Either<Errors, ValidateUserOutput> output = validateUserOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }
}
