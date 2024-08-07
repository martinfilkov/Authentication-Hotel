package com.tinqinacademy.authentication.rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    @PostMapping("api/auth/login")
    public String test(){
        return "Success";
    }

    @PostMapping("/api/auth/demote")
    public String testAuthenticated(){
        return "Failure";
    }
}
