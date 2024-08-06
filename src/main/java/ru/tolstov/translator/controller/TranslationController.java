package ru.tolstov.translator.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tolstov.translator.service.TranslationService;

@RestController
@AllArgsConstructor
@RequestMapping("/translate")
public class TranslationController {
    private TranslationService translationService;
    @PostMapping("")
    public ResponseEntity<String> translate(
            @RequestBody Text text,
            @RequestParam(name = "from") String sourceLanguage,
            @RequestParam(name = "to") String targetLanguage,
            HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String translation = translationService.translate(text.getText(), sourceLanguage, targetLanguage, ipAddress);
        return ResponseEntity.ok(translation);
    }

    @Data
    public static class Text {
        private String text;
    }
}
