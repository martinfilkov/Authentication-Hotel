package com.tinqinacademy.authentication.api.operations.base;

import java.util.List;

public class AdminPaths {
    private static final List<String> ADMIN_PATHS = List.of(
            AuthenticationMappings.DEMOTE_USER,
            AuthenticationMappings.PROMOTE_USER
    );

    public static List<String> getPaths() {
        return ADMIN_PATHS;
    }
}
