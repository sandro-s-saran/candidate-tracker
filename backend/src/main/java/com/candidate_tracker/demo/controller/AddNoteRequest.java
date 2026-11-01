package com.candidate_tracker.demo.controller;

import jakarta.validation.constraints.NotBlank;

public record AddNoteRequest(@NotBlank String text) {}
