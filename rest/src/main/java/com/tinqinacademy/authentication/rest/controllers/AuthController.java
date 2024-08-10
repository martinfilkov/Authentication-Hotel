package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserOutput;
import com.tinqinacademy.authentication.core.services.operations.LoginUserOperationProcessor;
import com.tinqinacademy.authentication.core.services.operations.RegisterUserOperationProcessor;
import com.tinqinacademy.authentication.core.services.operations.ValidateUserOperationProcessor;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ValidateUserOperationProcessor validateUserOperationProcessor;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully validated user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(AuthenticationMappings.REGISTER_USER)
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserInput input) {
        Either<Errors, RegisterUserOutput> output = registerUserOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.LOGIN_USER)
    public ResponseEntity<?> loginUser(@RequestBody LoginUserInput input, HttpServletRequest request) {
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
            @ApiResponse(responseCode = "201", description = "Successfully validated user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(AuthenticationMappings.VALIDATE_TOKEN)
    public ResponseEntity<?> validateUser(@RequestBody ValidateUserInput input) {
        Either<Errors, ValidateUserOutput> output = validateUserOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }
}
