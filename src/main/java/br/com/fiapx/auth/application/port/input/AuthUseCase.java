package br.com.fiapx.auth.application.port.input;

import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;

public interface AuthUseCase {
    String login(String email, String password);
    User register(String email, String username, String password, UserRole role);
}
