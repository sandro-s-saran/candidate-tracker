package com.candidate_tracker.demo.domain;
import java.time.Instant;

public record Note(String text, Instant createdAt) {}
