package com.candidate_tracker.demo.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CandidateTest {

    @Test
    void newCandidateStartsInAppliedStage() {
        Candidate candidate = new Candidate("Ada Lovelace", "ada@example.com", "Software Engineer");

        assertEquals(Stage.APPLIED, candidate.getStage());
    }
}