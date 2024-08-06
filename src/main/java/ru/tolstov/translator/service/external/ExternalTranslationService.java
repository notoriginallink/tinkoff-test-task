package ru.tolstov.translator.service.external;

import ru.tolstov.translator.service.TranslationFailException;

public interface ExternalTranslationService {
    String translate(String input, String sourceLanguage, String targetLanguage) throws TranslationFailException;
}
