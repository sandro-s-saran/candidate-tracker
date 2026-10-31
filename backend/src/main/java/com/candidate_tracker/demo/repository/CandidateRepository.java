package com.candidate_tracker.demo.repository;

import java.util.List;
import java.util.Optional;

import com.candidate_tracker.demo.domain.Candidate;

public interface CandidateRepository {
	Candidate save(Candidate c);
	List<Candidate> findAll();
	Optional<Candidate> findById(Long id);
	void deleteById(Long id);

}
