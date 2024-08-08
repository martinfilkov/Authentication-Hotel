package com.tinqinacademy.authentication.api.operations.operations.generate;

import com.tinqinacademy.authentication.api.operations.base.OperationOutput;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GenerateTokenOutput implements OperationOutput {
    private UserDetails userDetails;
}
