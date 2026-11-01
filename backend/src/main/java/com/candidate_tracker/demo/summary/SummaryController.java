package com.candidate_tracker.demo.summary;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.candidate_tracker.demo.domain.Candidate;
import com.candidate_tracker.demo.service.CandidateService;

@RestController
@RequestMapping("/api/candidates/{id}/summary")
public class SummaryController {

    private final CandidateService candidateService;
    private final CandidateSummaryService summaryService;

    public SummaryController(CandidateService candidateService, CandidateSummaryService summaryService) {
        this.candidateService = candidateService;
        this.summaryService = summaryService;
    }

    @PostMapping
    public SummaryResult summarise(@PathVariable Long id) {
        Candidate candidate = candidateService.getCandidate(id);
        return summaryService.summarise(candidate.getResumeText());
    }
}