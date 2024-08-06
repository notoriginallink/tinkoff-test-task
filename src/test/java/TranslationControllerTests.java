import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tolstov.translator.TranslatorApp;
import ru.tolstov.translator.model.TranslationRequest;
import ru.tolstov.translator.repository.TranslationRequestRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TranslatorApp.class)
@AutoConfigureMockMvc
public class TranslationControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TranslationRequestRepository requestRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void translate_WhenValidRequest_ShouldReturn200AndSaveToDb() throws Exception {
        String body = "{\n\"text\": \"Hello, I am Danya!\"\n}";
        String expectedResult = "Здравствуйте, Я являюсь Даня!";
        String actualResult = mvc.perform(post("/translate")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .param("from", "en")
                .param("to", "ru"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedResult, actualResult);
        Mockito.verify(requestRepository, Mockito.times(1)).save(Mockito.any(TranslationRequest.class));
    }

    @Test
    public void translate_WhenMissingParam_ShouldReturn400() throws Exception {
        String body = "{\n\"text\": \"Hello, I am Danya!\"}";
        mvc.perform(post("/translate")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .param("from", "en"))
                .andExpect(status().isBadRequest());
        Mockito.verify(requestRepository, Mockito.never()).save(Mockito.any(TranslationRequest.class));
    }

    @Test
    public void translate_WhenEmptyBody_ShouldReturn400() throws Exception {
        mvc.perform(post("/translate")
                .content("")
                .contentType(MediaType.APPLICATION_JSON)
                .param("from", "en")
                .param("to", "ru"))
                .andExpect(status().isBadRequest());
        Mockito.verify(requestRepository, Mockito.never()).save(Mockito.any(TranslationRequest.class));
    }
}
