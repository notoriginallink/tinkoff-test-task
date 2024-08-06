package ru.tolstov.translator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tolstov.translator.model.TranslationRequest;
import ru.tolstov.translator.repository.TranslationRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@AllArgsConstructor
public class ThreadedTranslationService implements TranslationService {
    private TranslationRequestRepository translationRequestRepository;
    private YandexTranslateService yandexTranslateService;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public String translate(String input, String sourceLanguage, String targetLanguage, String ip) throws TranslationFailException {
        var words = input.split(" ");
        List<Future<String>> translatedWords = new ArrayList<>(words.length);
        for (int i = 0; i < words.length; i++)
            translatedWords.add(null);

        for (int i = 0; i < words.length; i++) {
            var word = words[i];
            Future<String> translatedWord = executor.submit(() -> yandexTranslateService.translate(word, sourceLanguage, targetLanguage)
            );
            translatedWords.set(i, translatedWord);
        }

        String result = joinFutureStrings(translatedWords);

        saveTranslationRequest(input, result, sourceLanguage, targetLanguage, ip);

        return result;
    }

    private void saveTranslationRequest(String input, String result, String sourceLanguage, String targetLanguage, String ip) {
        var request = TranslationRequest.builder()
                .userIp(ip)
                .input(input)
                .result(result)
                .sourceLanguage(sourceLanguage)
                .targetLanguage(targetLanguage)
                .build();
        translationRequestRepository.save(request);
    }

    private String joinFutureStrings(List<Future<String>> futureList) {
        var builder = new StringBuilder();
        for (Future<String> translatedWord : futureList) {
            try {
                String word = translatedWord.get();
                builder.append(word);
                builder.append(" ");
            } catch (Exception e) {
                throw new TranslationFailException(e);
            }
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
