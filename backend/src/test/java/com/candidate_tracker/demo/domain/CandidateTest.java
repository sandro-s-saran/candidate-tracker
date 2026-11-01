package com.candidate_tracker.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CandidateTest {

    private static final Instant T1 = Instant.parse("2026-11-01T09:00:00Z");
    private static final Instant T2 = Instant.parse("2026-11-01T10:30:00Z");

    private Candidate candidate;

    @BeforeEach
    void setUp() {
        candidate = new Candidate("Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);
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
    void rejectsNullStage() {
        assertThrows(NullPointerException.class, () -> candidate.moveTo(null));
    }

    @Test
    void addNoteStoresTextAndTimestamp() {
        candidate.addNote("Strong phone screen", T1);

        Note note = candidate.getNotes().get(0);
        assertEquals("Strong phone screen", note.text());
        assertEquals(T1, note.createdAt());
    }

    @Test
    void notesKeepTheOrderTheyWereAdded() {
        candidate.addNote("First", T1);
        candidate.addNote("Second", T2);

        assertEquals("First", candidate.getNotes().get(0).text());
        assertEquals("Second", candidate.getNotes().get(1).text());
    }

    @Test
    void notesCannotBeRemoved() {
        candidate.addNote("First", T1);

        assertThrows(UnsupportedOperationException.class, () -> candidate.getNotes().clear());
    }
}