package br.com.fiapx.auth.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void create_shouldGenerateIdAndTimestamps() {
        User user = User.create("test@test.com", "testuser", "hash123", UserRole.USER);

        assertThat(user.id()).isNotNull();
        assertThat(user.email()).isEqualTo("test@test.com");
        assertThat(user.username()).isEqualTo("testuser");
        assertThat(user.passwordHash()).isEqualTo("hash123");
        assertThat(user.role()).isEqualTo(UserRole.USER);
        assertThat(user.createdAt()).isNotNull();
        assertThat(user.updatedAt()).isNotNull();
    }

    @Test
    void create_shouldAcceptAdminRole() {
        User user = User.create("admin@test.com", "admin", "hash", UserRole.ADMIN);
        assertThat(user.role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void constructor_shouldThrow_whenEmailIsNull() {
        assertThatThrownBy(() -> new User(UUID.randomUUID(), null, "user", "hash",
                UserRole.USER, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void constructor_shouldThrow_whenEmailIsBlank() {
        assertThatThrownBy(() -> new User(UUID.randomUUID(), "  ", "user", "hash",
                UserRole.USER, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void constructor_shouldThrow_whenUsernameIsNull() {
        assertThatThrownBy(() -> new User(UUID.randomUUID(), "a@b.com", null, "hash",
                UserRole.USER, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void constructor_shouldThrow_whenUsernameIsBlank() {
        assertThatThrownBy(() -> new User(UUID.randomUUID(), "a@b.com", "", "hash",
                UserRole.USER, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void constructor_shouldThrow_whenPasswordHashIsNull() {
        assertThatThrownBy(() -> new User(UUID.randomUUID(), "a@b.com", "user", null,
                UserRole.USER, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void constructor_shouldThrow_whenPasswordHashIsBlank() {
        assertThatThrownBy(() -> new User(UUID.randomUUID(), "a@b.com", "user", "  ",
                UserRole.USER, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void constructor_shouldThrow_whenRoleIsNull() {
        assertThatThrownBy(() -> new User(UUID.randomUUID(), "a@b.com", "user", "hash",
                null, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role");
    }
}
