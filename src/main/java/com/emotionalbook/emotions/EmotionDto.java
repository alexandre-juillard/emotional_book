package com.emotionalbook.emotions;

/**
 * DTO représentant une émotion de la taxonomie.
 */
public record EmotionDto(
        Integer id,
        String nom,
        String niveau
) {}

