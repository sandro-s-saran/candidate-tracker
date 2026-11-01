package com.candidate_tracker.demo.controller;

import com.candidate_tracker.demo.domain.Stage;

import jakarta.validation.constraints.NotNull;

public record MoveStageRequest(@NotNull Stage stage) {}
