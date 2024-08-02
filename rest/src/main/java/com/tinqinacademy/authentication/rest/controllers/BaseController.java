package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import com.tinqinacademy.authentication.api.operations.base.OperationOutput;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class BaseController {
    public <T extends OperationOutput> ResponseEntity<?> handleResponse(Either<Errors, T> either, HttpStatus status) {
        if (either.isLeft()) {
            Errors errors = either.getLeft();
            return new ResponseEntity<>(errors, HttpStatusCode.valueOf(errors.getCode()));
        }
        return new ResponseEntity<>(either.get(), status);
    }
}
