package com.tinqinacademy.authentication.api.operations.operations.validate;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ValidateUserInput implements OperationInput {
    @NotBlank(message = "Token cannot be null")
    private String token;

    @NotBlank(message = "Username cannot be null")
    private String username;
}
