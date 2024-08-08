package com.tinqinacademy.authentication.api.operations.exceptions;

import com.tinqinacademy.authentication.api.operations.base.Errors;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ErrorWrapper implements Errors {
    private List<ErrorResponse> errors;
    private LocalDate timestamp;
    private Integer code;
}
