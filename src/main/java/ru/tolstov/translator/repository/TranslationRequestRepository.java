package ru.tolstov.translator.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.tolstov.translator.config.DatabaseConfig;
import ru.tolstov.translator.model.TranslationRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class TranslationRequestRepository {
    private DatabaseConfig databaseConfig;
    public void save(TranslationRequest translationRequest) {
        try (Connection connection = databaseConfig.getConnection()) {
            String query = "INSERT INTO translation.translation_requests VALUES (DEFAULT, ?, ?, ?, ?, ?);";
            PreparedStatement st = connection.prepareStatement(query);
            st.setString(1, translationRequest.getUserIp());
            st.setString(2, translationRequest.getSourceLanguage());
            st.setString(3, translationRequest.getTargetLanguage());
            st.setString(4, translationRequest.getInput());
            st.setString(5, translationRequest.getResult());

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
