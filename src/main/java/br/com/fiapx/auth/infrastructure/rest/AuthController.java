package br.com.fiapx.auth.infrastructure.rest;

import br.com.fiapx.auth.application.port.input.AuthUseCase;
import br.com.fiapx.auth.infrastructure.rest.dto.LoginRequest;
import br.com.fiapx.auth.infrastructure.rest.dto.LoginResponse;
import br.com.fiapx.auth.infrastructure.rest.dto.RegisterRequest;
import br.com.fiapx.auth.infrastructure.rest.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login and registration endpoints")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String token = authUseCase.login(request.email(), request.password());
        return LoginResponse.of(token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        var user = authUseCase.register(request.email(), request.username(), request.password(), request.role());
        return UserResponse.fromDomain(user);
    }
}
