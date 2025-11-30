package ru.tolstov.translator.controller;

import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<TranslateResponse> translate(
        @RequestBody TranslateRequest translateRequest,
        @RequestParam(name = "from") String sourceLanguage,
        @RequestParam(name = "to") String targetLanguage,
        HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        String translation = translationService.translate(translateRequest.getText(), sourceLanguage, targetLanguage, ipAddress);

        TranslateResponse response = new TranslateResponse();
        response.setText(translation);
        return ResponseEntity.ok(response);
    }

    @Data
    public static class TranslateRequest {
        private String text;
    }

    @Data
    public static class TranslateResponse {
        private String text;
    }
}
