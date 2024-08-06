package ru.tolstov.translator.service.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.tolstov.translator.service.TranslationFailException;

import java.util.List;

@Service
public class YandexTranslateService implements ExternalTranslationService {
    @Value("${yandex.api.iam-token}")
    private String IAM_TOKEN;
    @Value("${yandex.api.folder.id}")
    private String FOLDER_ID;
    @Value("${yandex.api.url}")
    private String URL;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String translate(String input, String sourceLanguage, String targetLanguage) throws TranslationFailException {
        var headers = configureHeaders();
        var body = configureBody(new String[] {input}, sourceLanguage, targetLanguage);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (HttpStatusCodeException e) {
            try {
                var errorMessage = mapper.readTree(e.getResponseBodyAsString()).get("message").asText();
                throw new TranslationFailException(errorMessage);
            } catch (JsonProcessingException e2) {
                throw new RuntimeException(e2.getMessage());
            }
        }

        try {
            TranslationResponse responseBody = mapper.readValue(response.getBody(), TranslationResponse.class);
            return responseBody.getTranslations().get(0).getText();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private HttpHeaders configureHeaders() {
        var headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + IAM_TOKEN);

        return headers;
    }

    private String configureBody(String[] input, String sourceLanguage, String targetLanguage) {
        try {
            var request = new TranslationRequest(sourceLanguage, targetLanguage, FOLDER_ID, input);
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter @Setter @AllArgsConstructor
    private static class TranslationRequest {
        private String sourceLanguageCode;
        private String targetLanguageCode;
        private String folderId;
        private String[] texts;
    }

    @Getter @Setter
    private static class TranslationResponse {
        private List<Translation> translations;
        @Getter @Setter
        private static class Translation {
            private String text;
        }
    }
}
