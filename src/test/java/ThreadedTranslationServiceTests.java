import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.tolstov.translator.repository.TranslationRequestRepository;
import ru.tolstov.translator.service.ThreadedTranslationService;
import ru.tolstov.translator.service.TranslationFailException;
import ru.tolstov.translator.service.external.ExternalTranslationService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ThreadedTranslationServiceTests {
    @Mock
    private ExternalTranslationService externalTranslationService;
    @Mock
    private TranslationRequestRepository translationRequestRepository;
    @InjectMocks
    private static ThreadedTranslationService service;

    @BeforeEach
    public void init() {
        openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("translateValidProvider")
    public void translate_WhenValidString_ShouldReturnTranslated(String input, String expectedResult) {
        String srcLanguage = "a";
        String trgLanguage = "b";
        String[] inputWords = input.split(" ");
        String[] expectedOutputWords = expectedResult.split(" ");
        for (int i = 0; i < expectedOutputWords.length; i++)
            Mockito.doReturn(expectedOutputWords[i]).when(externalTranslationService).translate(inputWords[i], srcLanguage, trgLanguage);

        String actualResult = service.translate(input, srcLanguage, trgLanguage, "ip");
        assertEquals(actualResult, expectedResult);
    }

    public static Stream<Object[]> translateValidProvider() {
        return Stream.of(
                new Object[] {"Я Даня", "I Danya"},
                new Object[] {"Привет, как дела", "Hi, how affairs"}
        );
    }

    @Test
    public void translate_WhenCompoundWords_ShouldReturnTranslated() {
        String srcLanguage = "de";
        String trgLanguage = "ru";
        String text = "Hallo, heute ist mein Geburtstag!";
        String expectedResult = "Привет, Сегодня есть мой День рождения!";

        Mockito.doReturn("Привет,").when(externalTranslationService).translate("Hallo,", srcLanguage, trgLanguage);
        Mockito.doReturn("Сегодня").when(externalTranslationService).translate("heute", srcLanguage, trgLanguage);
        Mockito.doReturn("есть").when(externalTranslationService).translate("ist", srcLanguage, trgLanguage);
        Mockito.doReturn("мой").when(externalTranslationService).translate("mein", srcLanguage, trgLanguage);
        Mockito.doReturn("День рождения!").when(externalTranslationService).translate("Geburtstag!", srcLanguage, trgLanguage);

        String actualResult = service.translate(text, srcLanguage, trgLanguage, "ip");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void translate_WhenEmptyOrBlankString_shouldThrowException() {
        String empty = "";
        String blank = "   ";

        assertThrowsExactly(TranslationFailException.class, () -> service.translate(empty, "en", "ru", "ip"));
        assertThrowsExactly(TranslationFailException.class, () -> service.translate(blank, "en", "ru", "ip"));
    }

    @Test
    public void translate_WhenLanguageNotFound_shouldThrowException() {
        String exceptionMessage = "UnknownLanguage";
        String text = "Sample";
        String validLanguage = "en";
        String invalidLanguage = "zzz";
        Mockito.doThrow(new TranslationFailException(exceptionMessage))
                .when(externalTranslationService)
                .translate(text, validLanguage, invalidLanguage);

        assertThrowsExactly(TranslationFailException.class, () -> service.translate(text, validLanguage, invalidLanguage, "ip"), exceptionMessage);
    }


}
