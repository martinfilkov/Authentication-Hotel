package com.tinqinacademy.authentication.api.operations.operations.authenticate;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AuthenticateUserInput implements OperationInput {
    private String token;
}
