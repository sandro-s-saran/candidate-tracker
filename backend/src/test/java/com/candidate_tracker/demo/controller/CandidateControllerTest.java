package com.candidate_tracker.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.candidate_tracker.demo.domain.Candidate;
import com.candidate_tracker.demo.domain.Stage;
import com.candidate_tracker.demo.repository.InMemoryCandidateRepository;
import com.candidate_tracker.demo.service.CandidateService;

class CandidateControllerTest {

    private static final Instant NOW = Instant.parse("2026-11-01T09:00:00Z");

    private CandidateService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = new CandidateService(new InMemoryCandidateRepository(), Clock.fixed(NOW, ZoneOffset.UTC));
        mockMvc = MockMvcBuilders.standaloneSetup(new CandidateController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private Candidate createCandidate(String name, String role) {
        return service.createCandidate(name, "test@example.com", role, null, null, null);
    }

    @Test
    void createCandidateReturns201AndTheNewCandidate() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ada Lovelace", "email": "ada@example.com", "roleAppliedFor": "Software Engineer"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.stage").value("APPLIED"));
    }

    @Test
    void createCandidateWithBlankNameReturns400() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "email": "ada@example.com", "roleAppliedFor": "Software Engineer"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCandidateWithInvalidEmailReturns400() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ada Lovelace", "email": "not-an-email", "roleAppliedFor": "Software Engineer"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCandidateReturnsTheDetails() throws Exception {
        Candidate ada = createCandidate("Ada Lovelace", "Software Engineer");

        mockMvc.perform(get("/api/candidates/{id}", ada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"));
    }

    @Test
    void getUnknownCandidateReturns404() throws Exception {
        mockMvc.perform(get("/api/candidates/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void listReturnsAllCandidates() throws Exception {
        createCandidate("Ada Lovelace", "Software Engineer");
        createCandidate("Grace Hopper", "Data Analyst");

        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listFiltersByStage() throws Exception {
        createCandidate("Ada Lovelace", "Software Engineer");
        Candidate grace = createCandidate("Grace Hopper", "Data Analyst");
        service.moveCandidate(grace.getId(), Stage.INTERVIEW);

        mockMvc.perform(get("/api/candidates").param("stage", "INTERVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Grace Hopper"));
    }

    @Test
    void listFiltersByRole() throws Exception {
        createCandidate("Ada Lovelace", "Software Engineer");
        createCandidate("Grace Hopper", "Data Analyst");

        mockMvc.perform(get("/api/candidates").param("role", "data analyst"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Grace Hopper"));
    }

    @Test
    void listCanBeSortedByStage() throws Exception {
        Candidate ada = createCandidate("Ada Lovelace", "Software Engineer");
        Candidate grace = createCandidate("Grace Hopper", "Data Analyst");
        service.moveCandidate(grace.getId(), Stage.OFFER);

        mockMvc.perform(get("/api/candidates").param("sort", "STAGE"))
        		.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Grace Hopper"))
                .andExpect(jsonPath("$[1].name").value("Ada Lovelace"));
    }

    @Test
    void moveStageUpdatesTheCandidate() throws Exception {
        Candidate ada = createCandidate("Ada Lovelace", "Software Engineer");

        mockMvc.perform(patch("/api/candidates/{id}/stage", ada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\": \"OFFER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stage").value("OFFER"));
    }

    @Test
    void moveStageToAnUnknownStageReturns400() throws Exception {
        Candidate ada = createCandidate("Ada Lovelace", "Software Engineer");

        mockMvc.perform(patch("/api/candidates/{id}/stage", ada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\": \"BANANA\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void moveStageOfUnknownCandidateReturns404() throws Exception {
        mockMvc.perform(patch("/api/candidates/{id}/stage", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\": \"OFFER\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addNoteReturns201AndTheNoteIsOnTheCandidate() throws Exception {
        Candidate ada = createCandidate("Ada Lovelace", "Software Engineer");

        mockMvc.perform(post("/api/candidates/{id}/notes", ada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Strong phone screen\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notes[0].text").value("Strong phone screen"));
    }

    @Test
    void addBlankNoteReturns400() throws Exception {
        Candidate ada = createCandidate("Ada Lovelace", "Software Engineer");

        mockMvc.perform(post("/api/candidates/{id}/notes", ada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"  \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReturns204AndTheCandidateIsGone() throws Exception {
        Candidate ada = createCandidate("Ada Lovelace", "Software Engineer");

        mockMvc.perform(delete("/api/candidates/{id}", ada.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/candidates/{id}", ada.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void createCandidateAcceptsOptionalDetails() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ada Lovelace", "email": "ada@example.com", "roleAppliedFor": "Software Engineer",
                                 "phone": "0400 000 000", "source": "LinkedIn", "resumeText": "Built a compiler in Java."}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").value("0400 000 000"))
                .andExpect(jsonPath("$.source").value("LinkedIn"))
                .andExpect(jsonPath("$.resumeText").value("Built a compiler in Java."));
    }
}