package com.emotionalbook.entries;

import com.emotionalbook.entries.dto.EntreeEmotionDto;
import com.emotionalbook.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/entries", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Entrées émotionnelles", description = "CRUD basique des entrées émotionnelles du MVP")
@SecurityRequirement(name = "bearerAuth")
public class EntriesController {

    private final EntriesRepository repository;

    public EntriesController(EntriesRepository repository) {
        this.repository = repository;
    }

    public static record CreerEntreeEmotionRequete(
            @NotNull Integer emotionPrincipaleId,
            @NotNull @Min(1) @Max(10) Integer intensite,
            @Parameter(description = "Date/heure ISO de l'entrée; défaut = maintenant")
            LocalDateTime dateHeure,
            @Size(max = 2000) String note
    ) {}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une entrée émotionnelle")
    public ResponseEntity<EntreeEmotionDto> creer(@AuthenticationPrincipal User me,
                                                  @Valid @RequestBody CreerEntreeEmotionRequete req) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        LocalDateTime date = (req.dateHeure() != null ? req.dateHeure() : LocalDateTime.now());
        EntreeEmotionDto created = repository.creer(me.getId(), req.emotionPrincipaleId(), req.intensite(), date, req.note());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister mes entrées émotionnelles par période")
    public List<EntreeEmotionDto> lister(@AuthenticationPrincipal User me,
                                         @RequestParam(name = "from", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                         @RequestParam(name = "to", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return repository.lister(me.getId(), from, to);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une de mes entrées émotionnelles par id")
    public ResponseEntity<EntreeEmotionDto> details(@AuthenticationPrincipal User me, @PathVariable("id") Long id) {
        return repository.trouverParId(id)
                .filter(e -> e.utilisateurId().equals(me.getId()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une de mes entrées émotionnelles par id")
    public ResponseEntity<Void> supprimer(@AuthenticationPrincipal User me, @PathVariable("id") Long id) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        var opt = repository.trouverParId(id);
        if (opt.isEmpty() || !opt.get().utilisateurId().equals(me.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        boolean ok = repository.supprimer(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
