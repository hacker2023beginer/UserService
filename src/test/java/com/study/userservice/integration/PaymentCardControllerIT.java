package com.study.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PaymentCardControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private Long createUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Max",
                          "surname": "Payne",
                          "birthDate": "1991-01-01",
                          "email": "max@test.com"
                        }
                        """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createCard(Long userId) throws Exception {
        String response = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "number": "1234567812345678",
                          "holder": "MAX PAYNE",
                          "expirationDate": "2030-12-31",
                          "userId": %d
                        }
                        """.formatted(userId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void createCard_success() throws Exception {
        Long userId = createUser();

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "number": "1234567812345678",
                          "holder": "MAX PAYNE",
                          "expirationDate": "2030-12-31",
                          "userId": %d
                        }
                        """.formatted(userId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getCardById_success() throws Exception {
        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(get("/cards/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId));
    }

    @Test
    void updateCard_success() throws Exception {
        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(put("/cards/" + cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "number": "9999888877776666",
                          "holder": "UPDATED",
                          "expirationDate": "2035-01-01",
                          "userId": %d
                        }
                        """.formatted(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("9999888877776666"))
                .andExpect(jsonPath("$.holder").value("UPDATED"));
    }

    @Test
    void activateCard_success() throws Exception {
        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(patch("/cards/" + cardId + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivateCard_success() throws Exception {
        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(patch("/cards/" + cardId + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void deleteCard_success() throws Exception {
        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(delete("/cards/" + cardId))
                .andExpect(status().isNoContent());
    }

    @Test
    void maxFiveCards_limitEnforced() throws Exception {
        Long userId = createUser();
        for (int i = 0; i < 5; i++) {
            createCard(userId);
        }
        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "number": "0000111122223333",
                          "holder": "OVER LIMIT",
                          "expirationDate": "2030-01-01",
                          "userId": %d
                        }
                        """.formatted(userId)))
                .andExpect(status().isBadRequest());
    }
}
