package com.candidate_tracker.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.candidate_tracker.demo.domain.Candidate;
import com.candidate_tracker.demo.domain.Stage;
import com.candidate_tracker.demo.service.CandidateService;
import com.candidate_tracker.demo.service.SortField;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate create(@Valid @RequestBody CreateCandidateRequest request) {
        return service.createCandidate(request.name(), request.email(), request.roleAppliedFor(), 
        		request.phone(), request.source(), request.resumeText());
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Candidate getCandidate(@PathVariable Long id) {
    	return service.getCandidate(id);
    }
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Candidate> getAllCandidates(Stage stage,  String role,  SortField sortBy) {
    	return service.list(stage, role, sortBy);
    }
    
    @PatchMapping("/{id}/stage")
    @ResponseStatus(HttpStatus.OK)
    public Candidate updateStage(@PathVariable Long id, @Valid @RequestBody MoveStageRequest request) {
    	service.getCandidate(id).moveTo(request.stage());
    	return service.getCandidate(id);
    }
    
    @PostMapping("/{id}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate postNote(@PathVariable Long id, @Valid @RequestBody AddNoteRequest request) {
    	service.addNote(id, request.text());
    	return service.getCandidate(id);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCandidate(@PathVariable Long id) {
    	service.deleteCandidate(id);
    }
}