package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.api.operations.operations.logout.LogoutUserInput;
import com.tinqinacademy.authentication.persistence.entities.BlacklistToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogoutUserInputToBlackListTokenConverter implements Converter<LogoutUserInput, BlacklistToken> {
    @Override
    public BlacklistToken convert(LogoutUserInput input) {
        log.info("Start converting from LogoutUserInput to BlacklistToken with input: {}", input);

        BlacklistToken output = BlacklistToken.builder()
                .token(input.getToken())
                .build();

        log.info("End converting from LogoutUserInput to BlacklistToken with output: {}", output);
        return output;
    }
}
