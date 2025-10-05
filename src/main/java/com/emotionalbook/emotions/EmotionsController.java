package com.emotionalbook.emotions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/emotions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Émotions", description = "Lecture de la taxonomie des émotions")
public class EmotionsController {

    private final EmotionsRepository repository;

    public EmotionsController(EmotionsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Lister les émotions", description = "Optionnellement filtrer par niveau: primaire, secondaire ou tertiaire")
    public List<EmotionDto> lister(
            @Parameter(description = "Niveau de la taxonomie: primaire|secondaire|tertiaire")
            @RequestParam(name = "level", required = false) String level
    ) {
        return repository.lister(level);
    }
}

