package ru.tolstov.translator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tolstov.translator.service.TranslationFailException;

@RestControllerAdvice
public class CustomControllerExceptionHandler {
    @ExceptionHandler(TranslationFailException.class)
    public ResponseEntity<String> translationFailException(TranslationFailException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
