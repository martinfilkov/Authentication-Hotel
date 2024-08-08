package com.tinqinacademy.authentication.api.operations.operations.extract;

import com.tinqinacademy.authentication.api.operations.base.OperationOutput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ExtractUserOutput implements OperationOutput {
    private String username;
}
