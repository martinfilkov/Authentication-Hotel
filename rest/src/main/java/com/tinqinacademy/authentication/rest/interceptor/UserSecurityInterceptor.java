package com.tinqinacademy.authentication.rest.interceptor;

import com.tinqinacademy.authentication.api.operations.base.AdminPaths;
import com.tinqinacademy.authentication.core.services.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.authentication.rest.context.LoggedUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSecurityInterceptor implements HandlerInterceptor {
    private final LoggedUser loggedUser;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String jwtToken = authorization.substring(7);
        if (!jwtService.isTokenValid(jwtToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        Claims claims = jwtService.extractAllClaims(jwtToken);
        String userId = claims.get("user_id").toString();
        Optional<User> userOptional = userRepository.findById(UUID.fromString(userId));
        if (userOptional.isEmpty()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        if (isAdminPath(request.getRequestURI()) && !userOptional.get().getRoleType().equals(RoleType.ADMIN)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        loggedUser.setLoggedUser(userOptional.get());
        loggedUser.setToken(jwtToken);
        return true;
    }


    private boolean isAdminPath(String requestUri) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return AdminPaths.getPaths().stream()
                .anyMatch(path -> antPathMatcher.match(path, requestUri));
    }
}
