package com.tinqinacademy.authentication.api.operations.operations.authenticate;

import com.tinqinacademy.authentication.api.operations.base.OperationOutput;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AuthenticateUserOutput implements OperationOutput {
    private UserDetails userDetails;
}
