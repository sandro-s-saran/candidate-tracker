package com.candidate_tracker.demo.summary;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.candidate_tracker.demo.controller.ApiExceptionHandler;
import com.candidate_tracker.demo.domain.Candidate;
import com.candidate_tracker.demo.repository.InMemoryCandidateRepository;
import com.candidate_tracker.demo.service.CandidateService;

class SummaryControllerTest {

    private static final Instant NOW = Instant.parse("2026-11-01T09:00:00Z");

    private CandidateService candidateService;

    @BeforeEach
    void setUp() {
        candidateService = new CandidateService(
                new InMemoryCandidateRepository(), Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private MockMvc mockMvcWith(TextGenerator generator) {
        SummaryController controller =
                new SummaryController(candidateService, new CandidateSummaryService(generator));
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void generatesASummaryForACandidateWithResumeText() throws Exception {
        Candidate ada = candidateService.createCandidate(
                "Ada Lovelace", "ada@example.com", "Software Engineer",
                null, null, "Five years of Java and SQL.");

        mockMvcWith((systemPrompt, userContent) -> "Skilled Java developer.")
                .perform(post("/api/candidates/{id}/summary", ada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("GENERATED"))
                .andExpect(jsonPath("$.text").value("Skilled Java developer."));
    }

    @Test
    void candidateWithoutResumeTextHasNothingToSummarise() throws Exception {
        Candidate ada = candidateService.createCandidate(
                "Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);

        mockMvcWith((systemPrompt, userContent) -> {
            throw new IllegalStateException("should not be called");
        })
                .perform(post("/api/candidates/{id}/summary", ada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_RESUME_TEXT"))
                .andExpect(jsonPath("$.text").doesNotExist());
    }

    @Test
    void providerFailureIsReportedAsUnavailable() throws Exception {
        Candidate ada = candidateService.createCandidate(
                "Ada Lovelace", "ada@example.com", "Software Engineer",
                null, null, "Five years of Java and SQL.");

        mockMvcWith((systemPrompt, userContent) -> {
            throw new IllegalStateException("provider down");
        })
                .perform(post("/api/candidates/{id}/summary", ada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNAVAILABLE"));
    }

    @Test
    void unknownCandidateReturns404() throws Exception {
        mockMvcWith((systemPrompt, userContent) -> "unused")
                .perform(post("/api/candidates/{id}/summary", 999))
                .andExpect(status().isNotFound());
    }
}