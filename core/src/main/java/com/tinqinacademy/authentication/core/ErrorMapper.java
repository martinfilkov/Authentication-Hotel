package com.tinqinacademy.authentication.core;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import com.tinqinacademy.authentication.api.operations.exceptions.ErrorResponse;
import com.tinqinacademy.authentication.api.operations.exceptions.ErrorWrapper;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ErrorMapper {
    public ErrorWrapper handleError(Throwable ex, HttpStatusCode statusCode) {
        return ErrorWrapper
                .builder()
                .errors(List.of(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build()))
                .code(statusCode.value())
                .build();
    }

    public ErrorWrapper handleViolations(Set<ConstraintViolation<OperationInput>> violations, HttpStatusCode statusCode) {
        List<ErrorResponse> responses = violations.stream()
                .map(v -> ErrorResponse.builder()
                        .message(v.getMessage())
                        .build())
                .toList();

        return ErrorWrapper.builder()
                .errors(responses)
                .code(statusCode.value())
                .build();
    }
}
