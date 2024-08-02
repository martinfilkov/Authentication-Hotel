package com.tinqinacademy.authentication.api.operations.base;

import io.vavr.control.Either;

public interface OperationProcessor<S extends OperationInput, T extends OperationOutput>{
    Either<Errors,T> process(S input);
}
