package com.candidate_tracker.demo.service;

public class CandidateNotFoundException extends RuntimeException {

    public CandidateNotFoundException(Long id) {
        super("Candidate not found with id " + id);
    }
}