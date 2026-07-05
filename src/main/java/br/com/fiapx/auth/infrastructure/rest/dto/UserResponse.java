package br.com.fiapx.auth.infrastructure.rest.dto;

import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(UUID id, String email, String username, UserRole role, LocalDateTime createdAt) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(user.id(), user.email(), user.username(), user.role(), user.createdAt());
    }
}
