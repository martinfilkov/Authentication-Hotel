package com.tinqinacademy.authentication.api.operations.operations.change;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class ChangePasswordInput implements OperationInput {
    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "New password cannot be null")
    private String newPassword;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @JsonIgnore
    private String userId;
}
