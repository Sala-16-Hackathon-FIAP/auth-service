package br.com.fiapx.auth.infrastructure.persistence;

import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UserEntity() {}

    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.id = user.id();
        entity.email = user.email();
        entity.username = user.username();
        entity.passwordHash = user.passwordHash();
        entity.role = user.role();
        entity.createdAt = user.createdAt();
        entity.updatedAt = user.updatedAt();
        return entity;
    }

    public User toDomain() {
        return new User(id, email, username, passwordHash, role, createdAt, updatedAt);
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
