package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenInput;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenOutput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import feign.Headers;
import feign.RequestLine;

@Headers({"Content-Type: application/json"})
public interface AuthenticationRestClient {
    @RequestLine("POST /api/auth/authenticate")
    AuthenticateUserOutput authenticateUser(AuthenticateUserInput input);

    @RequestLine("POST /api/auth/generate-token")
    GenerateTokenOutput generateToken(GenerateTokenInput input);

    @RequestLine("POST /api/auth/register")
    RegisterUserOutput registerUser(RegisterUserInput input);

    @RequestLine("POST /api/auth/login")
    LoginUserOutput loginUser(LoginUserInput input);
}
