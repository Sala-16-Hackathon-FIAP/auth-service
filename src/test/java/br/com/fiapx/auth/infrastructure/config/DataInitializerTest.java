package br.com.fiapx.auth.infrastructure.config;

import br.com.fiapx.auth.application.port.output.UserRepositoryPort;
import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ApplicationArguments args;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_shouldCreateAdminUser_whenNotExists() {
        when(userRepository.existsByEmail("useradmin@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Admin@12345")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run(args);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void run_shouldSkip_whenAdminAlreadyExists() {
        when(userRepository.existsByEmail("useradmin@email.com")).thenReturn(true);

        dataInitializer.run(args);

        verify(userRepository, never()).save(any());
    }
}
