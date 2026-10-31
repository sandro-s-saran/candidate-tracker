package com.candidate_tracker.demo.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

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
    void canAddNotetoCandidate() {
    	candidate.addNote("Candidate was good.");
    	assertEquals("Candidate was good.", candidate.getNote());
    	candidate.addNote("Call candidate back");
    	assertEquals("Candidate was good.\n\nCall candidate back", candidate.getNote());
    	
    	
    }
}