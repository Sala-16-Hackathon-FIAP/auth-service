package br.com.fiapx.auth.application.service;

import br.com.fiapx.auth.application.port.input.AuthUseCase;
import br.com.fiapx.auth.application.port.output.UserRepositoryPort;
import br.com.fiapx.auth.domain.exception.InvalidCredentialsException;
import br.com.fiapx.auth.domain.exception.UserAlreadyExistsException;
import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(password, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return jwtService.generateToken(user);
    }

    @Override
    @Transactional
    public User register(String email, String username, String password, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("email", email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("username", username);
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.create(email, username, encodedPassword, role);
        return userRepository.save(user);
    }
}
