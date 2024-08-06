package ru.tolstov.translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.tolstov.translator.repository.TranslationRequestRepository;
import ru.tolstov.translator.service.ThreadedTranslationService;
import ru.tolstov.translator.service.YandexTranslateService;

@SpringBootApplication
public class TranslatorApp {
    public static void main(String[] args) throws Exception {
//        var repo = new TranslationRequestRepository();
//        var yan = new YandexTranslateService();
//        var serv = new ThreadedTranslationService(repo, yan);
//        String result = serv.translate("Привет, дорогой", "ru", "de", "rere");
//        System.out.println(result);
        SpringApplication.run(TranslatorApp.class, args);
    }
}
