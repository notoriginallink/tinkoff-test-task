package ru.tolstov.translator.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TranslationRequest {
    private UUID id;
    private String userIp;
    private String input;
    private String result;
    private String sourceLanguage;
    private String targetLanguage;
}
