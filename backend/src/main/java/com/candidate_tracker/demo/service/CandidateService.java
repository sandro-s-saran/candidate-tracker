package com.candidate_tracker.demo.service;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;

import com.candidate_tracker.demo.domain.Candidate;
import com.candidate_tracker.demo.domain.Stage;
import com.candidate_tracker.demo.repository.CandidateRepository;

public class CandidateService {
	private CandidateRepository cr;
	private Clock clock;
	
	public CandidateService(CandidateRepository cr, Clock clock) {
		this.cr = cr;
		this.clock = clock;
	}
	
	public Candidate createCandidate(String name, String role, String email) {
		Candidate candidate = new Candidate(name, role, email);
		candidate.setCreatedAt(clock.instant());
		cr.save(candidate);
		return candidate;
	}
	
	public Candidate getCandidate(Long id) {
		return cr.findById(id)
		        .orElseThrow(() -> new CandidateNotFoundException(id));
	}
	
	public List<Candidate> list(Stage stage, String role, SortField sortBy) {

	    Comparator<Candidate> newestFirst =
	            Comparator.comparing(Candidate::getCreatedAt).reversed();

	    Comparator<Candidate> order = (sortBy == SortField.STAGE)
	            ? Comparator.comparing(Candidate::getStage).thenComparing(newestFirst)
	            : newestFirst;

	    return cr.findAll().stream()
	            .filter(c -> stage == null || c.getStage() == stage)
	            .filter(c -> role == null || c.getRoleAppliedFor().equalsIgnoreCase(role))
	            .sorted(order)
	            .toList();
	}
	
	public void moveCandidate(Long id, Stage stage) {
		Candidate candidate = this.getCandidate(id);
		candidate.moveTo(stage);
	}
	
	public void addNote(Long id, String note) {
		Candidate candidate = this.getCandidate(id);
		candidate.addNote(note, clock.instant());
	}
	
	public void deleteCandidate(Long id) {
		Candidate candidate = this.getCandidate(id);
		cr.deleteById(id);
	}
	
	

}
