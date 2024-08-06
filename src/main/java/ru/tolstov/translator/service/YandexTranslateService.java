package ru.tolstov.translator.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class YandexTranslateService {
    private final String IAM_TOKEN = "t1.9euelZqSkp2YzZ6VnYuTjZKPi4-Rz-3rnpWazczLzY6Nz56TlpPPkZWdjpDl8_dUBjhK-e8RFF92_t3z9xQ1NUr57xEUX3b-zef1656Vmo2ZlZOXm83MxoyWlsvIycrK7_zF656Vmo2ZlZOXm83MxoyWlsvIycrK.KsxUpptp2gN1ktUyyUtpXYEqekihan6AJiAusEsbW9jm6zgC2fXC1qxqKDoItE-dzWQ9P39RWtes3GiGipXYCw";
    private final String FOLDER_ID = "b1gff7ju57fv99s8j960";
    private final String URL = "https://translate.api.cloud.yandex.net/translate/v2/translate";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

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
                throw new RuntimeException(e2);
            }
        }

        try {
            TranslationResponse responseBody = mapper.readValue(response.getBody(), TranslationResponse.class);
            return responseBody.getTranslations().get(0).getText();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        @JsonProperty("translations")
        private List<Translation> translations;
        @Getter @Setter
        private static class Translation {
            @JsonProperty("text")
            private String text;
        }
    }
}
