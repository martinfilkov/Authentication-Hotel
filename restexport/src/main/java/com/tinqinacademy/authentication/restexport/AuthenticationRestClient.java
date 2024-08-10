package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.authenticate.AuthenticateUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenInput;
import com.tinqinacademy.authentication.api.operations.operations.generate.GenerateTokenOutput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserInput;
import com.tinqinacademy.authentication.api.operations.operations.login.LoginUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserOutput;
import feign.Headers;
import feign.RequestLine;

@Headers({"Content-Type: application/json"})
public interface AuthenticationRestClient {
    @RequestLine("POST /api/auth/validate-token")
    ValidateUserOutput validateUser(ValidateUserInput input);
}
