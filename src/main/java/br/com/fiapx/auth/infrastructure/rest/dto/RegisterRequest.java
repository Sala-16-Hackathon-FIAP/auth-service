package br.com.fiapx.auth.infrastructure.rest.dto;

import br.com.fiapx.auth.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 3, max = 100) String username,
    @NotBlank @Size(min = 8, max = 100) String password,
    UserRole role
) {
    public RegisterRequest {
        if (role == null) role = UserRole.USER;
    }
}
