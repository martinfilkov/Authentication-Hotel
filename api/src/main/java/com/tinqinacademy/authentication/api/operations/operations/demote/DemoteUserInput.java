package com.tinqinacademy.authentication.api.operations.operations.demote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.authentication.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class DemoteUserInput implements OperationInput {
    @NotBlank(message = "User id cannot be blank")
    private String userId;

    @JsonIgnore
    private String loggedUserId;
}
