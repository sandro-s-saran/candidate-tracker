package com.candidate_tracker.demo.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;


import java.time.Instant;

class CandidateTest {
	
	private Candidate candidate;

	@BeforeEach
	void setUp() {
	    candidate = new Candidate("Ada Lovelace", "ada@example.com", "Software Engineer");
	}

    @Test
    void newCandidateStartsInAppliedStage() {

        assertEquals(Stage.APPLIED, candidate.getStage());
    }
    
    @Test
    void canMoveToAnyStageNotJustTheNextOne() {
        candidate.moveTo(Stage.OFFER);

        assertEquals(Stage.OFFER, candidate.getStage());
    }
    
    @Test
    void addNoteStoresTextAndTimestamp() {
        Instant time = Instant.parse("2026-11-01T09:00:00Z");

        candidate.addNote("Strong phone screen", time);

        Note note = candidate.getNotes().get(0);
        assertEquals("Strong phone screen", note.text());
        assertEquals(time, note.createdAt());
    }
    
}