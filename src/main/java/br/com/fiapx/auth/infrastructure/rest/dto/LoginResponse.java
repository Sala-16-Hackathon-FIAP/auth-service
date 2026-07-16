package br.com.fiapx.auth.infrastructure.rest.dto;

public record LoginResponse(String bearerToken, String tokenType) {
    public static LoginResponse of(String token) {
        return new LoginResponse(token, "Bearer");
    }
}
