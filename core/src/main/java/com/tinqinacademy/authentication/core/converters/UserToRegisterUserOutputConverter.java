package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserOutput;
import com.tinqinacademy.authentication.persistence.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserToRegisterUserOutputConverter implements Converter<User, RegisterUserOutput> {
    @Override
    public RegisterUserOutput convert(User input) {
        log.info("Start converting from User to RegisterUserOutput with input: {}", input);

        RegisterUserOutput output = RegisterUserOutput.builder()
                .id(input.getId())
                .build();

        log.info("End converting from User to RegisterUserOutput with output: {}", output);
        return output;
    }
}
