package com.emotionalbook.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profil", description = "Lecture et mise à jour du profil utilisateur")
@SecurityRequirement(name = "bearerAuth")
public class ProfilController {

    private final UserRepository users;

    public ProfilController(UserRepository users) {
        this.users = users;
    }

    public static record ProfilUpdateRequest(
            @Size(max = 100) String firstName,
            @Size(max = 100) String lastName,
            @Size(max = 10) String locale,
            @Size(max = 50) String timezone
    ) {}

    @GetMapping
    @Operation(summary = "Récupérer le profil de l'utilisateur authentifié")
    public ResponseEntity<?> moi(@AuthenticationPrincipal User me) {
        if (me == null) return ResponseEntity.status(401).body(Map.of("message", "Non authentifié"));
        return ResponseEntity.ok(Map.of(
                "id", me.getId(),
                "email", me.getEmail(),
                "firstName", me.getFirstName(),
                "lastName", me.getLastName(),
                "locale", me.getLocale(),
                "timezone", me.getTimezone()
        ));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour le profil")
    public ResponseEntity<?> mettreAJour(@AuthenticationPrincipal User me, @Valid @RequestBody ProfilUpdateRequest req) {
        if (me == null) return ResponseEntity.status(401).body(Map.of("message", "Non authentifié"));
        users.mettreAJourProfil(me.getId(), req.firstName(), req.lastName(), req.locale(), req.timezone());
        var maj = users.trouverParId(me.getId()).orElse(me);
        return ResponseEntity.ok(Map.of(
                "id", maj.getId(),
                "email", maj.getEmail(),
                "firstName", maj.getFirstName(),
                "lastName", maj.getLastName(),
                "locale", maj.getLocale(),
                "timezone", maj.getTimezone()
        ));
    }
}
