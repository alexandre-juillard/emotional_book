package com.emotionalbook;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Requête invalide");
        pd.setType(URI.create("https://httpstatuses.com/400"));
        Map<String, String> erreurs = new HashMap<>();
        for (var err : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(err.getField(), messageErreur(err));
        }
        pd.setProperty("erreurs", erreurs);
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail generique(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Erreur interne");
        pd.setType(URI.create("https://httpstatuses.com/500"));
        pd.setDetail("Une erreur inattendue est survenue.");
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }

    private String messageErreur(FieldError fe) {
        if (fe.getDefaultMessage() != null) return fe.getDefaultMessage();
        return "Champ invalide";
        }
}

