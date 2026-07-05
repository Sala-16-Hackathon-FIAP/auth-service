package br.com.fiapx.auth.application.port.output;

import br.com.fiapx.auth.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
