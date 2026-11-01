package com.candidate_tracker.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.candidate_tracker.demo.service.CandidateNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CandidateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(CandidateNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}