package br.com.fiapx.auth.infrastructure.config;

import br.com.fiapx.auth.application.port.output.UserRepositoryPort;
import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "useradmin@email.com";
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        User admin = User.create(adminEmail, "useradmin", passwordEncoder.encode("Admin@12345"), UserRole.ADMIN);
        userRepository.save(admin);
        log.info("Default admin user created: {} (password: Admin@12345)", adminEmail);
    }
}
