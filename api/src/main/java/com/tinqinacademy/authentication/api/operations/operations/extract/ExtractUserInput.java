package com.tinqinacademy.authentication.api.operations.operations.extract;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ExtractUserInput implements OperationInput {
    @NotBlank(message = "Token cannot be null")
    private String token;
}
