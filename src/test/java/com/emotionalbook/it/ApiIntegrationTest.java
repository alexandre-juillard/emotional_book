package com.emotionalbook.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
        "spring.flyway.enabled=true"
})
class ApiIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("emotionalbook");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "4");
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    @DisplayName("Parcours intégration: GET /emotions, POST/GET/DELETE /entries")
    void parcoursComplet() throws Exception {
        jdbc.update("INSERT INTO users(email, first_name, last_name) VALUES (?,?,?)", "it@example.com", "Int", "Test");
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE email=?", Long.class, "it@example.com");

        Integer emotionId = jdbc.queryForObject("SELECT id FROM emotion_taxonomy WHERE level='primaire' LIMIT 1", Integer.class);
        assertThat(emotionId).isNotNull();

        String emotionsJson = mvc.perform(get("/emotions").param("level", "primaire"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        JsonNode arr = mapper.readTree(emotionsJson);
        assertThat(arr.isArray()).isTrue();
        assertThat(arr.size()).isGreaterThan(0);

        String body = "{\n" +
                "  \"utilisateurId\": " + userId + ",\n" +
                "  \"emotionPrincipaleId\": " + emotionId + ",\n" +
                "  \"intensite\": 5,\n" +
                "  \"note\": \"journée correcte\"\n" +
                "}";
        String createdJson = mvc.perform(post("/entries").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        JsonNode created = mapper.readTree(createdJson);
        long entryId = created.path("id").asLong();
        assertThat(entryId).isPositive();
        assertThat(created.path("utilisateurId").asLong()).isEqualTo(userId);
        assertThat(created.path("emotionPrincipaleId").asInt()).isEqualTo(emotionId);
        assertThat(created.path("intensite").asInt()).isEqualTo(5);

        String listJson = mvc.perform(get("/entries").param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        JsonNode list = mapper.readTree(listJson);
        assertThat(list.isArray()).isTrue();
        assertThat(list.size()).isGreaterThan(0);
        assertThat(list.findValuesAsText("id")).contains(String.valueOf(entryId));

        mvc.perform(get("/entries/" + entryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entryId));

        mvc.perform(delete("/entries/" + entryId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/entries/" + entryId))
                .andExpect(status().isNotFound());
    }
}

