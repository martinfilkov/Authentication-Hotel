package com.tinqinacademy.authentication.api.operations.operations.logout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class LogoutUserInput implements OperationInput {
    @JsonIgnore
    private String token;
}
