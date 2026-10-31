package com.candidate_tracker.demo.domain;

public class Candidate {
    private final String name;
    private final String email;
    private final String roleAppliedFor;
    private Stage stage = Stage.APPLIED;

    public Candidate(String name, String email, String roleAppliedFor) {
        this.name = name;
        this.email = email;
        this.roleAppliedFor = roleAppliedFor;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void moveTo(Stage stage) {
    	this.stage = stage;
    }
}