package com.emotionalbook.entries.dto;

import java.time.LocalDateTime;

/**
 * Représente une entrée émotionnelle retournée par l'API.
 */
public record EntreeEmotionDto(
        Long id,
        Long utilisateurId,
        Integer emotionPrincipaleId,
        String emotionPrincipaleNom,
        Integer intensite,
        LocalDateTime dateHeure,
        String note
) {}

