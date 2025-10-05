package com.emotionalbook.web;

import com.emotionalbook.emotions.EmotionsController;
import com.emotionalbook.emotions.EmotionsRepository;
import com.emotionalbook.entries.EntriesController;
import com.emotionalbook.entries.EntriesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {EmotionsController.class, EntriesController.class})
class ApiWebTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    EmotionsRepository emotionsRepository;

    @MockBean
    EntriesRepository entriesRepository;

    @Test
    @DisplayName("GET /emotions retourne 200 avec JSON")
    void emotionsOk() throws Exception {
        when(emotionsRepository.lister(null)).thenReturn(java.util.List.of());
        mvc.perform(get("/emotions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /entries avec intensité invalide retourne 400")
    void entriesValidation() throws Exception {
        String body = "{\n" +
                "  \"emotionPrincipaleId\": 1,\n" +
                "  \"intensite\": 50\n" +
                "}";
        mvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}

