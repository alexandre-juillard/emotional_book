package com.emotionalbook.entries;

import com.emotionalbook.entries.dto.EntreeEmotionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/entries", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Entrées émotionnelles", description = "CRUD basique des entrées émotionnelles du MVP")
public class EntriesController {

    private final EntriesRepository repository;

    public EntriesController(EntriesRepository repository) {
        this.repository = repository;
    }

    public static record CreerEntreeEmotionRequete(
            @Parameter(description = "Identifiant utilisateur (temporaire tant que l'auth n'est pas en place)")
            Long utilisateurId,
            @NotNull Integer emotionPrincipaleId,
            @NotNull @Min(1) @Max(10) Integer intensite,
            @Parameter(description = "Date/heure ISO de l'entrée; défaut = maintenant")
            LocalDateTime dateHeure,
            @Size(max = 2000) String note
    ) {}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une entrée émotionnelle")
    public ResponseEntity<EntreeEmotionDto> creer(@Valid @RequestBody CreerEntreeEmotionRequete req,
                                                  @RequestHeader(name = "X-User-Id", required = false) Long userHeader) {
        Long userId = (req.utilisateurId() != null ? req.utilisateurId() : (userHeader != null ? userHeader : 1L));
        LocalDateTime date = (req.dateHeure() != null ? req.dateHeure() : LocalDateTime.now());
        EntreeEmotionDto created = repository.creer(userId, req.emotionPrincipaleId(), req.intensite(), date, req.note());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister les entrées émotionnelles par période")
    public List<EntreeEmotionDto> lister(@RequestHeader(name = "X-User-Id", required = false) Long userHeader,
                                         @RequestParam(name = "userId", required = false) Long userQuery,
                                         @RequestParam(name = "from", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                         @RequestParam(name = "to", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        Long userId = userQuery != null ? userQuery : (userHeader != null ? userHeader : 1L);
        return repository.lister(userId, from, to);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une entrée émotionnelle par id")
    public ResponseEntity<EntreeEmotionDto> details(@PathVariable("id") Long id) {
        return repository.trouverParId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une entrée émotionnelle par id")
    public ResponseEntity<Void> supprimer(@PathVariable("id") Long id) {
        boolean ok = repository.supprimer(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

