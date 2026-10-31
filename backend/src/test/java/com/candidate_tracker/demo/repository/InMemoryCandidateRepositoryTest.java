package com.candidate_tracker.demo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.candidate_tracker.demo.domain.Candidate;

class InMemoryCandidateRepositoryTest {

    private InMemoryCandidateRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCandidateRepository();
    }

    private Candidate newCandidate(String name) {
        return new Candidate(name, "test@example.com", "Software Engineer");
    }

    @Test
    void saveAssignsAnId() {
        Candidate saved = repository.save(newCandidate("Ada Lovelace"));

        assertNotNull(saved.getId());
    }

    @Test
    void savedCandidatesGetDifferentIds() {
        Candidate first = repository.save(newCandidate("Ada Lovelace"));
        Candidate second = repository.save(newCandidate("Grace Hopper"));

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void findByIdReturnsTheSavedCandidate() {
        Candidate saved = repository.save(newCandidate("Ada Lovelace"));

        assertEquals(saved, repository.findById(saved.getId()).orElseThrow());
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void findAllReturnsEverythingSaved() {
        repository.save(newCandidate("Ada Lovelace"));
        repository.save(newCandidate("Grace Hopper"));

        List<Candidate> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void deleteRemovesTheCandidate() {
        Candidate saved = repository.save(newCandidate("Ada Lovelace"));

        repository.deleteById(saved.getId());

        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void idsAreNotReusedAfterADelete() {
        Candidate first = repository.save(newCandidate("Ada Lovelace"));
        Candidate second = repository.save(newCandidate("Grace Hopper"));
        repository.deleteById(first.getId());

        Candidate third = repository.save(newCandidate("Alan Turing"));

        assertNotEquals(second.getId(), third.getId());
    }
}