package com.emotionalbook.security;

import com.emotionalbook.users.User;
import com.emotionalbook.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentification", description = "Inscription et connexion par JWT")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final PasswordPolicy passwordPolicy;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt, PasswordPolicy passwordPolicy) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.passwordPolicy = passwordPolicy;
    }

    public static record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 12, max = 100) String password,
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName
    ) {}

    public static record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un compte local", description = "Politique de mot de passe: au moins 12 caractères, incluant au moins une minuscule, une majuscule, un chiffre et un caractère spécial.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte créé"),
            @ApiResponse(responseCode = "400", description = "Mot de passe non conforme ou données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        Optional<User> existing = users.trouverParEmail(req.email());
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email déjà utilisé"));
        }
        if (!passwordPolicy.estValide(req.password())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", passwordPolicy.exigences()));
        }
        String hash = encoder.encode(req.password());
        long id = users.creer(req.email(), hash, req.firstName(), req.lastName());
        String token = jwt.genererToken(id, req.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "token", token,
                "user", Map.of(
                        "id", id,
                        "email", req.email(),
                        "firstName", req.firstName(),
                        "lastName", req.lastName()
                )
        ));
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Se connecter avec email/mot de passe", description = "Endpoint soumis à un rate limiting par IP. Après plusieurs tentatives rapprochées, une réponse 429 peut être renvoyée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentifié"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "429", description = "Trop de requêtes (rate limiting)")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Optional<User> u = users.trouverParEmail(req.email());
        if (u.isEmpty() || u.get().getPasswordHash() == null || !encoder.matches(req.password(), u.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Identifiants invalides"));
        }
        User user = u.get();
        String token = jwt.genererToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName()
                )
        ));
    }
}
