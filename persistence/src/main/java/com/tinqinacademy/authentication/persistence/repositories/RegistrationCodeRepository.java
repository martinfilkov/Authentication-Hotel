package com.tinqinacademy.authentication.persistence.repositories;

import com.tinqinacademy.authentication.persistence.entities.RegistrationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistrationCodeRepository extends JpaRepository<RegistrationCode, UUID> {
    Optional<RegistrationCode> findByEmail(String email);
    Optional<RegistrationCode> findByCode(String code);
}
