package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.extract.ExtractUserInput;
import com.tinqinacademy.authentication.api.operations.operations.extract.ExtractUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenInput;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenOutput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserOutput;
import com.tinqinacademy.authentication.core.services.operations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final AuthenticateUserOperationProcessor authenticateUserOperationProcessor;
    private final GenerateTokenOperationProcessor generateTokenOperationProcessor;
    private final RegisterUserOperationProcessor registerUserOperationProcessor;
    private final LoginUserOperationProcessor loginUserOperationProcessor;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully authenticated user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(AuthenticationMappings.AUTHENTICATE_USER)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticateUserInput input){
        Either<Errors, AuthenticateUserOutput> output = authenticateUserOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully validated user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(AuthenticationMappings.GENERATE_TOKEN)
    public ResponseEntity<?> generateToken(@RequestBody GenerateTokenInput input){
        Either<Errors, GenerateTokenOutput> output = generateTokenOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully validated user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(AuthenticationMappings.REGISTER_USER)
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserInput input){
        Either<Errors, RegisterUserOutput> output = registerUserOperationProcessor.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully logged user"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(value = AuthenticationMappings.LOGIN_USER)
    public ResponseEntity<?> loginUser(@RequestBody LoginUserInput input){
        Either<Errors, LoginUserOutput> output = loginUserOperationProcessor.process(input);
        if (output.isLeft()) {
            Errors errors = output.getLeft();
            return new ResponseEntity<>(errors, HttpStatusCode.valueOf(errors.getCode()));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + output.get().getToken());
        return new ResponseEntity<>(output.get(), headers, HttpStatus.CREATED);
    }
}
