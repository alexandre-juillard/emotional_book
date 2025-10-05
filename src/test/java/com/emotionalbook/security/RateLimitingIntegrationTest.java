package com.emotionalbook.security;

import com.emotionalbook.users.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({SecurityConfig.class, RateLimitingFilter.class})
@TestPropertySource(properties = {
        "security.rate-limit.login.capacity=2",
        "security.rate-limit.login.period-seconds=60",
        "security.rate-limit.global.capacity=1000",
        "security.rate-limit.global.period-seconds=60",
        "security.cors.allowed-origins=http://localhost"
})
class RateLimitingIntegrationTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter; // inséré par SecurityConfig

    @MockBean
    UserRepository userRepository; // utilisé par AuthController

    @MockBean
    JwtService jwtService; // injection dans AuthController

    @MockBean
    PasswordEncoder passwordEncoder; // injection dans AuthController

    @MockBean
    PasswordPolicy passwordPolicy; // injection dans AuthController

    @Test
    @DisplayName("/auth/login est limité: 3e requête => 429")
    void loginRateLimited() throws Exception {
        String ip = "9.9.9.9";
        String body = "{\n  \"email\": \"nope@example.com\",\n  \"password\": \"wrongPassword123!\"\n}";

        // Simuler utilisateur introuvable (peu importe le statut exact, on cible le 429 à la 3e requête)
        when(userRepository.trouverParEmail("nope@example.com")).thenReturn(java.util.Optional.empty());

        // 1 et 2: consommer les jetons
        mvc.perform(post("/auth/login").header("X-Forwarded-For", ip)
                        .contentType(APPLICATION_JSON).content(body))
                .andReturn();
        mvc.perform(post("/auth/login").header("X-Forwarded-For", ip)
                        .contentType(APPLICATION_JSON).content(body))
                .andReturn();

        // 3: doit être limité
        mvc.perform(post("/auth/login").header("X-Forwarded-For", ip)
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isTooManyRequests());
    }
}
