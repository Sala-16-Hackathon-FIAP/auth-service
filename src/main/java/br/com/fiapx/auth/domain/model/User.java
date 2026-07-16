package br.com.fiapx.auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
    UUID id,
    String email,
    String username,
    String passwordHash,
    UserRole role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public User {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email must not be blank");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username must not be blank");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("Password hash must not be blank");
        if (role == null) throw new IllegalArgumentException("Role must not be null");
    }

    public static User create(String email, String username, String passwordHash, UserRole role) {
        return new User(UUID.randomUUID(), email, username, passwordHash, role,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
