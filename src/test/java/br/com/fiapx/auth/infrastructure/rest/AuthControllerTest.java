package br.com.fiapx.auth.infrastructure.rest;

import br.com.fiapx.auth.application.port.input.AuthUseCase;
import br.com.fiapx.auth.domain.exception.InvalidCredentialsException;
import br.com.fiapx.auth.domain.exception.UserAlreadyExistsException;
import br.com.fiapx.auth.domain.model.User;
import br.com.fiapx.auth.domain.model.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(br.com.fiapx.auth.infrastructure.security.SecurityConfig.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthUseCase authUseCase;

    @Test
    void login_shouldReturn200WithToken_whenCredentialsAreValid() throws Exception {
        when(authUseCase.login("admin@test.com", "Admin@12345")).thenReturn("valid-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.com\",\"password\":\"Admin@12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value("valid-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        when(authUseCase.login(anyString(), anyString())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrong@test.com\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturn400_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn400_whenFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn201WithUser_whenDataIsValid() throws Exception {
        User createdUser = new User(UUID.randomUUID(), "new@test.com", "newuser",
                "hashed", UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
        when(authUseCase.register(anyString(), anyString(), anyString(), any())).thenReturn(createdUser);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@test.com\",\"username\":\"newuser\",\"password\":\"Secure@12345\",\"role\":\"USER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        when(authUseCase.register(anyString(), anyString(), anyString(), any()))
                .thenThrow(new UserAlreadyExistsException("email", "existing@test.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"existing@test.com\",\"username\":\"user\",\"password\":\"Secure@12345\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void register_shouldReturn400_whenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@test.com\",\"username\":\"user\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
    }
}
