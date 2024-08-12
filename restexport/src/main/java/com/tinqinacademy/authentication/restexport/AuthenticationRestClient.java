package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserInput;
import com.tinqinacademy.authentication.api.operations.operations.validate.ValidateUserOutput;
import feign.Headers;
import feign.RequestLine;

@Headers({"Content-Type: application/json"})
public interface AuthenticationRestClient {
    @RequestLine("POST /api/auth/validate-token")
    ValidateUserOutput validateToken(ValidateUserInput input);
}
