package com.candidate_tracker.demo.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class CandidateTest {
	
	private Candidate candidate;
	DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

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
    	String formattedDate1 = LocalDateTime.now().format(myFormatObj);
    	assertEquals(formattedDate1 + ": Candidate was good.\n\n", candidate.getNote());
    	candidate.addNote("Call candidate back");
    	String formattedDate2 = LocalDateTime.now().format(myFormatObj);
    	assertEquals(formattedDate1 + ": Candidate was good.\n\n" + formattedDate2 + ": Call candidate back\n\n", candidate.getNote());    	
    	
    }
}