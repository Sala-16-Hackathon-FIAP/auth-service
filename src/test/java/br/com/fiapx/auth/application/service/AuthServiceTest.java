package br.com.fiapx.auth.application.service;

import br.com.fiapx.auth.application.port.output.UserRepositoryPort;
import br.com.fiapx.auth.domain.exception.InvalidCredentialsException;
import br.com.fiapx.auth.domain.exception.UserAlreadyExistsException;
import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User(UUID.randomUUID(), "admin@test.com", "admin",
                "$2a$10$hashed", UserRole.ADMIN, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("password", "$2a$10$hashed")).thenReturn(true);
        when(jwtService.generateToken(adminUser)).thenReturn("jwt-token");

        String token = authService.login("admin@test.com", "password");

        assertThat(token).isEqualTo("jwt-token");
        verify(jwtService).generateToken(adminUser);
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("unknown@test.com", "password"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_shouldThrow_whenPasswordWrong() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login("admin@test.com", "wrongpass"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void register_shouldSaveUser_whenEmailAndUsernameAreNew() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User created = authService.register("new@test.com", "newuser", "password", UserRole.USER);

        assertThat(created.email()).isEqualTo("new@test.com");
        assertThat(created.username()).isEqualTo("newuser");
        assertThat(created.passwordHash()).isEqualTo("encoded");
        assertThat(created.role()).isEqualTo(UserRole.USER);
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("admin@test.com", "other", "pass", UserRole.USER))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void register_shouldThrow_whenUsernameAlreadyExists() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("new@test.com", "admin", "pass", UserRole.USER))
                .isInstanceOf(UserAlreadyExistsException.class);
    }
}
