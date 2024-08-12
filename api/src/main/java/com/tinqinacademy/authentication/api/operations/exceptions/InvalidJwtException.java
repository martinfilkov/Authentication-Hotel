package com.tinqinacademy.authentication.api.operations.exceptions;

public class InvalidJwtException extends CustomException{
    public InvalidJwtException(String message) {
        super(message);
    }
}
