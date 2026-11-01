package com.candidate_tracker.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.candidate_tracker.demo.domain.Candidate;
import com.candidate_tracker.demo.domain.Note;
import com.candidate_tracker.demo.domain.Stage;
import com.candidate_tracker.demo.repository.InMemoryCandidateRepository;

class CandidateServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-01T09:00:00Z");

    private InMemoryCandidateRepository repository;
    private CandidateService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCandidateRepository();
        service = new CandidateService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private Candidate save(String name, String role, Stage stage, int hoursAgo) {
        Candidate candidate = new Candidate(name, "test@example.com", role, null, null, null);
        candidate.moveTo(stage);
        candidate.setCreatedAt(NOW.minus(hoursAgo, ChronoUnit.HOURS));
        return repository.save(candidate);
    }

    // Ada: oldest. Alan: newest.
    private void saveThreeCandidates() {
        save("Ada Lovelace", "Software Engineer", Stage.APPLIED, 3);
        save("Grace Hopper", "Data Analyst", Stage.INTERVIEW, 2);
        save("Alan Turing", "Software Engineer", Stage.INTERVIEW, 1);
    }

    private List<String> names(List<Candidate> candidates) {
        return candidates.stream().map(Candidate::getName).toList();
    }

    @Test
    void createCandidateSavesItWithIdStageAndDateAdded() {
        Candidate created = service.createCandidate("Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);

        assertNotNull(created.getId());
        assertEquals(Stage.APPLIED, created.getStage());
        assertEquals(NOW, created.getCreatedAt());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void getCandidateReturnsTheCandidate() {
        Candidate created = service.createCandidate("Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);

        assertEquals(created, service.getCandidate(created.getId()));
    }

    @Test
    void getCandidateThrowsWhenNotFound() {
        assertThrows(CandidateNotFoundException.class, () -> service.getCandidate(999L));
    }

    @Test
    void listWithNoFiltersReturnsEveryone() {
        saveThreeCandidates();

        List<Candidate> result = service.list(null, null, SortField.DATE_ADDED);

        assertEquals(3, result.size());
    }

    @Test
    void listFiltersByStage() {
        saveThreeCandidates();

        List<Candidate> result = service.list(Stage.INTERVIEW, null, SortField.DATE_ADDED);

        assertEquals(List.of("Alan Turing", "Grace Hopper"), names(result));
    }

    @Test
    void listFiltersByRoleIgnoringCase() {
        saveThreeCandidates();

        List<Candidate> result = service.list(null, "software engineer", SortField.DATE_ADDED);

        assertEquals(List.of("Alan Turing", "Ada Lovelace"), names(result));
    }
//
    @Test
    void listFiltersByStageAndRoleTogether() {
        saveThreeCandidates();

        List<Candidate> result = service.list(Stage.INTERVIEW, "Software Engineer", SortField.DATE_ADDED);

        assertEquals(List.of("Alan Turing"), names(result));
    }
//
    @Test
    void sortByDateAddedShowsNewestFirst() {
        saveThreeCandidates();

        List<Candidate> result = service.list(null, null, SortField.DATE_ADDED);

        assertEquals(List.of("Alan Turing", "Grace Hopper", "Ada Lovelace"), names(result));
    }

    @Test
    void sortByStageFollowsPipelineOrderThenNewestFirst() {
        saveThreeCandidates();

        List<Candidate> result = service.list(null, null, SortField.STAGE);

        assertEquals(List.of("Ada Lovelace", "Alan Turing", "Grace Hopper"), names(result));
    }

    @Test
    void moveCandidateChangesTheStage() {
        Candidate created = service.createCandidate("Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);

        service.moveCandidate(created.getId(), Stage.INTERVIEW);

        assertEquals(Stage.INTERVIEW, repository.findById(created.getId()).orElseThrow().getStage());
    }

    @Test
    void moveCandidateThrowsWhenNotFound() {
        assertThrows(CandidateNotFoundException.class, () -> service.moveCandidate(999L, Stage.OFFER));
    }

    @Test
    void addNoteTimestampsItWithTheServiceClock() {
        Candidate created = service.createCandidate("Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);

        service.addNote(created.getId(), "Strong phone screen");

        Note note = repository.findById(created.getId()).orElseThrow().getNotes().get(0);
        assertEquals("Strong phone screen", note.text());
        assertEquals(NOW, note.createdAt());
    }

    @Test
    void addNoteThrowsWhenNotFound() {
        assertThrows(CandidateNotFoundException.class, () -> service.addNote(999L, "Hello"));
    }

    @Test
    void deleteCandidateRemovesIt() {
        Candidate created = service.createCandidate("Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);

        service.deleteCandidate(created.getId());

        assertTrue(repository.findById(created.getId()).isEmpty());
    }
    
    @Test
    void createCandidateStoresOptionalDetails() {
        Candidate created = service.createCandidate("Ada Lovelace", "ada@example.com", "Software Engineer",
                "0400 000 000", "LinkedIn", "Built a compiler in Java.");

        assertEquals("0400 000 000", created.getPhone());
        assertEquals("LinkedIn", created.getSource());
        assertEquals("Built a compiler in Java.", created.getResumeText());
    }

    @Test
    void createCandidateWithoutOptionalDetailsLeavesThemEmpty() {
        Candidate created = service.createCandidate("Ada Lovelace", "ada@example.com", "Software Engineer", null, null, null);

        assertNull(created.getPhone());
        assertNull(created.getSource());
        assertNull(created.getResumeText());
    }
}