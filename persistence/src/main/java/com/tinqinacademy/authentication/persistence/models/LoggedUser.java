package com.tinqinacademy.authentication.persistence.models;

import com.tinqinacademy.authentication.persistence.entities.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Setter
@Builder
@Component
@RequestScope
public class LoggedUser {
    private User loggedUser;
}
