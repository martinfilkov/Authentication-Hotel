package com.tinqinacademy.authentication.api.operations.operations.recover;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RecoverPasswordInput implements OperationInput {
    @NotBlank(message = "Email cannot be blank")
    private String email;
}