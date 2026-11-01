package com.candidate_tracker.demo;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.candidate_tracker.demo.repository.CandidateRepository;
import com.candidate_tracker.demo.repository.InMemoryCandidateRepository;

@Configuration
public class AppConfig {

    @Bean
    public CandidateRepository candidateRepository() {
        return new InMemoryCandidateRepository();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}