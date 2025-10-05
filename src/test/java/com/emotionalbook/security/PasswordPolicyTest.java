package com.emotionalbook.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordPolicyTest {

    private final PasswordPolicy policy = new PasswordPolicy();

    @Test
    @DisplayName("Valide: >=12, avec minuscule, majuscule, chiffre, spécial")
    void valide() {
        assertThat(policy.estValide("Abcdef12345!"))
                .as("Doit être valide")
                .isTrue();
    }

    @Test
    @DisplayName("Invalide: < 12 caractères")
    void tropCourt() {
        assertThat(policy.estValide("Abcd1!efG"))
                .isFalse();
    }

    @Test
    @DisplayName("Invalide: pas de majuscule")
    void sansMajuscule() {
        assertThat(policy.estValide("abcdefg12345!"))
                .isFalse();
    }

    @Test
    @DisplayName("Invalide: pas de minuscule")
    void sansMinuscule() {
        assertThat(policy.estValide("ABCDEFG12345!"))
                .isFalse();
    }

    @Test
    @DisplayName("Invalide: pas de chiffre")
    void sansChiffre() {
        assertThat(policy.estValide("Abcdefghijk!"))
                .isFalse();
    }

    @Test
    @DisplayName("Invalide: pas de spécial")
    void sansSpecial() {
        assertThat(policy.estValide("Abcdefghijk1"))
                .isFalse();
    }
}

