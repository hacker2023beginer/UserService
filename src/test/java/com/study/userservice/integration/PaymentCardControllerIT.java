package com.study.userservice.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PaymentCardControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addCardToUser() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "name": "Max",
                  "surname": "Payne",
                  "birthDate": "1991-01-01",
                  "email": "max@test.com"
                }
                """));

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "number": "1234567812345678",
                  "holder": "MAX PAYNE",
                  "expirationDate": "2030-12-31",
                  "userId": 1
                }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }
}

