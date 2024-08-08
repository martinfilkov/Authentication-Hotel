package com.tinqinacademy.authentication.api.operations.operations.promote;

import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PromoteUserInput implements OperationInput {
    private String userId;
}
