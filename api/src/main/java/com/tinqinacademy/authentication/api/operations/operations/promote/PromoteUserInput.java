package com.tinqinacademy.authentication.api.operations.operations.promote;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PromoteUserInput implements OperationInput {
    @NotBlank(message = "User id cannot be blank")
    private String userId;
}
