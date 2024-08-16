package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.persistence.entities.RegistrationCode;
import com.tinqinacademy.email.api.operations.email.confirm.ConfirmEmailInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegistrationCodeToConfirmEmailInputConverter implements Converter<RegistrationCode, ConfirmEmailInput> {
    @Override
    public ConfirmEmailInput convert(RegistrationCode input) {
        log.info("Start converting from RegistrationCode to ConfirmEmailInput with input: {}", input);

        ConfirmEmailInput output = ConfirmEmailInput.builder()
                .emailTo(input.getEmail())
                .confirmationCode(input.getCode())
                .build();

        log.info("End converting from RegistrationCode to ConfirmEmail with output: {}", output);
        return output;
    }
}
