package com.tinqinacademy.authentication.api.operations.operations.register;

import com.tinqinacademy.authentication.api.operations.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RegisterUserOutput implements OperationOutput {
    private UUID id;
}
