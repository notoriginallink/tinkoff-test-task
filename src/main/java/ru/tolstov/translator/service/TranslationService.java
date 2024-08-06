package ru.tolstov.translator.service;

public interface TranslationService {
    String translate(String text, String sourceLanguage, String targetLanguage, String ip) throws TranslationFailException;
}
