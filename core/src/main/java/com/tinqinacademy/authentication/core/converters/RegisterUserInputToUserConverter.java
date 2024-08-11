package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.api.operations.operations.register.RegisterUserInput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegisterUserInputToUserConverter implements Converter<RegisterUserInput, User.UserBuilder> {
    @Override
    public User.UserBuilder convert(RegisterUserInput input) {
        log.info("Start converting from RegisterUserInput to User with input: {}", input);

        User.UserBuilder output = User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .roleType(RoleType.USER)
                .birthDate(input.getBirthDate())
                .phoneNumber(input.getPhoneNumber());

        log.info("End converting from RegisterUserInput to User with output: {}", output);
        return output;
    }
}
