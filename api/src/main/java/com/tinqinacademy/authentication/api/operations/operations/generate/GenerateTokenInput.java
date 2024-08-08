package com.tinqinacademy.authentication.api.operations.operations.generate;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GenerateTokenInput implements OperationInput {
    private String username;
}
