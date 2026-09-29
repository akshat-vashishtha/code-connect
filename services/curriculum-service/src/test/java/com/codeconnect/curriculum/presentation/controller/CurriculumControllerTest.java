package com.codeconnect.curriculum.presentation.controller;

import com.codeconnect.curriculum.application.dto.TrackResponse;
import com.codeconnect.curriculum.application.service.CurriculumService;
import com.codeconnect.curriculum.domain.enums.TrackStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(CurriculumController.class)
class CurriculumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurriculumService curriculumService;

    @MockBean
    private com.codeconnect.curriculum.infrastructure.config.InternalSecurityProperties internalSecurityProperties;

    @MockBean
    private com.codeconnect.curriculum.infrastructure.security.InternalSecurityValidator internalSecurityValidator;


    @Test
    @WithMockUser(username = "user-1", roles = {"STUDENT"})
    @DisplayName("GET /api/v1/curriculum/tracks returns 200 OK with track list")
    void getPublishedTracks_ShouldReturn200OK() throws Exception {
        TrackResponse track = new TrackResponse(
            "track-1",
            "Data Structures",
            "dsa",
            "Learn DSA",
            30,
            TrackStatus.PUBLISHED,
            Collections.emptyList(),
            Instant.now()
        );

        when(curriculumService.getPublishedTracks()).thenReturn(List.of(track));

        mockMvc.perform(get("/api/v1/curriculum/tracks")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("track-1"))
            .andExpect(jsonPath("$.data[0].title").value("Data Structures"));
    }
}
