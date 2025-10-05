package com.emotionalbook.security;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordPolicy {

    // Au moins 12 caractères, 1 minuscule, 1 majuscule, 1 chiffre, 1 spécial
    private static final Pattern STRONG_PWD = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{12,}$"
    );

    public boolean estValide(String motDePasse) {
        return motDePasse != null && STRONG_PWD.matcher(motDePasse).matches();
    }

    public String exigences() {
        return "Le mot de passe doit contenir au moins 12 caractères, incluant au moins une minuscule, une majuscule, un chiffre et un caractère spécial.";
    }
}

