package com.tinqinacademy.authentication.persistence.repositories;

import com.tinqinacademy.authentication.persistence.entities.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlackListTokenRepository extends JpaRepository<BlacklistToken, UUID> {
    Optional<BlacklistToken> findByToken(String token);
}
