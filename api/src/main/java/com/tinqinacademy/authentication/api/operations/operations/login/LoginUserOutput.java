package com.tinqinacademy.authentication.api.operations.operations.login;

import com.tinqinacademy.authentication.api.operations.base.OperationOutput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class LoginUserOutput implements OperationOutput {
    private String token;
}
