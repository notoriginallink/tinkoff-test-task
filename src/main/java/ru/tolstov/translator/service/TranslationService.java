package ru.tolstov.translator.service;

public interface TranslationService {
    String translate(String input, String sourceLanguage, String targetLanguage, String ip) throws TranslationFailException;
}
