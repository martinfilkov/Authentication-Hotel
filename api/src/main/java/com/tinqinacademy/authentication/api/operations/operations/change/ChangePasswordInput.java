package com.tinqinacademy.authentication.api.operations.operations.change;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChangePasswordInput implements OperationInput {
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password cannot be null")
    private String newPassword;
}
