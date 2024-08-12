package com.tinqinacademy.authentication.rest.interceptor;

import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.core.services.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.models.LoggedUser;
import com.tinqinacademy.authentication.persistence.models.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
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
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("UNAUTHORIZED Token is invalid or not provided");
            return false;
        }

        String jwtToken = authorization.substring(7);
        if (!jwtService.isTokenValid(jwtToken)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("UNAUTHORIZED Token is expired");
            return false;
        }

        Claims claims = jwtService.extractAllClaims(jwtToken);
        String userId = claims.get("user_id").toString();
        Optional<User> userOptional = userRepository.findById(UUID.fromString(userId));
        if (userOptional.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("UNAUTHORIZED User not found");
            return false;
        }

        if (request.getRequestURI().equals(AuthenticationMappings.DEMOTE_USER)
                || request.getRequestURI().equals(AuthenticationMappings.PROMOTE_USER)) {
            if (!userOptional.get().getRoleType().equals(RoleType.ADMIN)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("UNAUTHORIZED User must be admin to access this endpoint");
            }
        }

        loggedUser.setLoggedUser(userOptional.get());
        return true;
    }
}
