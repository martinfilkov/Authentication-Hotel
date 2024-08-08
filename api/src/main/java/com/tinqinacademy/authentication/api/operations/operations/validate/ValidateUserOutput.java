package com.tinqinacademy.authentication.api.operations.operations.validate;

import com.tinqinacademy.authentication.api.operations.base.OperationOutput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ValidateUserOutput implements OperationOutput {
    private boolean isValid;
}
