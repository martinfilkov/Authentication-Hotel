package com.tinqinacademy.authentication.persistence.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleType {
    USER("user"),
    ADMIN("admin"),
    UNKNOWN("");
    private final String code;

    RoleType(String code) {
        this.code = code;
    }

    @JsonCreator
    public static RoleType getByCode(String code) {
        return Arrays.stream(RoleType.values())
                .filter(type -> type.toString().equals(code))
                .findFirst()
                .orElse(RoleType.UNKNOWN);
    }

    @JsonValue
    public String toString() {
        return code;
    }
}
