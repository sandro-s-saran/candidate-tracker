package com.candidate_tracker.demo.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.candidate_tracker.demo.domain.Candidate;

public class InMemoryCandidateRepository implements CandidateRepository {

    private final Map<Long, Candidate> candidates = new LinkedHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public synchronized Candidate save(Candidate candidate) {
        if (candidate.getId() == null) {
            candidate.setId(nextId.getAndIncrement());
        }
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public synchronized List<Candidate> findAll() {
        return new ArrayList<>(candidates.values());
    }

    @Override
    public synchronized Optional<Candidate> findById(Long id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public synchronized void deleteById(Long id) {
        candidates.remove(id);
    }
}