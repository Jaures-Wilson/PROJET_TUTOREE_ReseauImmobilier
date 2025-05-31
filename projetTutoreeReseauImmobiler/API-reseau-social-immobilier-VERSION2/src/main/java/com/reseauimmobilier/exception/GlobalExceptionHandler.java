package com.reseauimmobilier.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().getSimpleName().equals("StatutPaiement")) {
            return ResponseEntity.badRequest().body(Map.of("erreur", "Statut de paiement invalide"));
        }

        return ResponseEntity.badRequest().body(Map.of("erreur", "Paramètre invalide"));
    }
}