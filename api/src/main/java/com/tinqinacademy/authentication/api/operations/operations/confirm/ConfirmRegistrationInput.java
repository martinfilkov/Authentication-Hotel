package com.tinqinacademy.authentication.api.operations.operations.confirm;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConfirmRegistrationInput implements OperationInput {
    @NotBlank(message = "Confirmation code cannot be blank")
    private String confirmationCode;
}
