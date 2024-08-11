package com.tinqinacademy.authentication.api.operations.operations.confirm;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConfirmRegistrationInput implements OperationInput {
    @NotBlank(message = "Confirmation code cannot be blank")
    @Size(max = 12, min = 12, message = "Code must be 12 characters")
    private String confirmationCode;
}
