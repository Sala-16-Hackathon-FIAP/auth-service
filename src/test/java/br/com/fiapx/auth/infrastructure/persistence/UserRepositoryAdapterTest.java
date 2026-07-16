package br.com.fiapx.auth.infrastructure.persistence;

import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(UserRepositoryAdapter.class)
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryAdapter repositoryAdapter;

    @Test
    void save_shouldPersistAndReturnUser() {
        User user = User.create("repo@test.com", "repouser", "encoded", UserRole.USER);

        User saved = repositoryAdapter.save(user);

        assertThat(saved.id()).isEqualTo(user.id());
        assertThat(saved.email()).isEqualTo("repo@test.com");
        assertThat(saved.username()).isEqualTo("repouser");
    }

    @Test
    void findByEmail_shouldReturnUser_whenExists() {
        User user = User.create("find@test.com", "finduser", "hash", UserRole.USER);
        repositoryAdapter.save(user);

        Optional<User> found = repositoryAdapter.findByEmail("find@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo("find@test.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = repositoryAdapter.findByEmail("nobody@test.com");
        assertThat(found).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnUser_whenExists() {
        User user = User.create("user@test.com", "uniquename", "hash", UserRole.USER);
        repositoryAdapter.save(user);

        Optional<User> found = repositoryAdapter.findByUsername("uniquename");

        assertThat(found).isPresent();
        assertThat(found.get().username()).isEqualTo("uniquename");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = repositoryAdapter.findByUsername("ghost");
        assertThat(found).isEmpty();
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        User user = User.create("byid@test.com", "byiduser", "hash", UserRole.ADMIN);
        repositoryAdapter.save(user);

        Optional<User> found = repositoryAdapter.findById(user.id());

        assertThat(found).isPresent();
        assertThat(found.get().role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = repositoryAdapter.findById(java.util.UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        repositoryAdapter.save(User.create("a@test.com", "auser", "hash", UserRole.USER));
        repositoryAdapter.save(User.create("b@test.com", "buser", "hash", UserRole.ADMIN));

        List<User> all = repositoryAdapter.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenExists() {
        repositoryAdapter.save(User.create("exists@test.com", "existsuser", "hash", UserRole.USER));
        assertThat(repositoryAdapter.existsByEmail("exists@test.com")).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenNotExists() {
        assertThat(repositoryAdapter.existsByEmail("nope@test.com")).isFalse();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenExists() {
        repositoryAdapter.save(User.create("u@test.com", "takenname", "hash", UserRole.USER));
        assertThat(repositoryAdapter.existsByUsername("takenname")).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenNotExists() {
        assertThat(repositoryAdapter.existsByUsername("freename")).isFalse();
    }
}
