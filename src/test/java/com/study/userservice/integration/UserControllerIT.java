package com.study.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIT {
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUser_fullFlow() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "name": "Ivan",
                  "surname": "Ivanov",
                  "birthDate": "1990-01-01",
                  "email": "ivan@test.com"
                }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getUserById() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "name": "Anna",
                  "surname": "Petrova",
                  "birthDate": "1992-02-02",
                  "email": "anna@test.com"
                }
                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();
        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("anna@test.com"));
    }

    @Test
    void updateUser_success() throws Exception {
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "Anna",
                      "surname": "Petrova",
                      "birthDate": "1992-02-02",
                      "email": "anna@test.com"
                    }
                    """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "AnnaUpdated",
                      "surname": "PetrovaUpdated",
                      "birthDate": "1992-02-02",
                      "email": "updated@test.com"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("AnnaUpdated"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    void updateUser_notFound() throws Exception {
        mockMvc.perform(put("/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "X",
                      "surname": "Y",
                      "birthDate": "1990-01-01",
                      "email": "x@test.com"
                    }
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_success() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "Test",
                      "surname": "User",
                      "birthDate": "1990-01-01",
                      "email": "test@test.com"
                    }
                    """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/users/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete("/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void activateUser_success() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "A",
                      "surname": "B",
                      "birthDate": "1990-01-01",
                      "email": "a@test.com"
                    }
                    """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(patch("/users/" + id + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivateUser_success() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "A",
                      "surname": "B",
                      "birthDate": "1990-01-01",
                      "email": "a@test.com"
                    }
                    """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(patch("/users/" + id + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void getUserCards_success() throws Exception {
        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "Card",
                      "surname": "Owner",
                      "birthDate": "1990-01-01",
                      "email": "card@test.com"
                    }
                    """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "number": "1111222233334444",
                      "holder": "Card Owner",
                      "expirationDate": "2030-01-01",
                      "userId": %d
                    }
                    """.formatted(userId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users/" + userId + "/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value("1111222233334444"))
                .andExpect(jsonPath("$[0].holder").value("Card Owner"));
    }

    @Test
    void getUserCards_userNotFound() throws Exception {
        mockMvc.perform(get("/users/9999/cards"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsers_paginationAndFiltering_success() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              "name": "Anna",
              "surname": "Petrova",
              "birthDate": "1992-02-02",
              "email": "anna@test.com"
            }
            """));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              "name": "Anna",
              "surname": "Ivanova",
              "birthDate": "1990-01-01",
              "email": "anna2@test.com"
            }
            """));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              "name": "Vlad",
              "surname": "Sidorov",
              "birthDate": "1995-05-05",
              "email": "vlad@test.com"
            }
            """));

        mockMvc.perform(get("/users")
                        .param("name", "Anna")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "surname,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].surname").value("Ivanova"))
                .andExpect(jsonPath("$.content[1].surname").value("Petrova"));
    }

    @Test
    void getUsers_emptyResult() throws Exception {
        mockMvc.perform(get("/users")
                        .param("name", "NonExistingName")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}

