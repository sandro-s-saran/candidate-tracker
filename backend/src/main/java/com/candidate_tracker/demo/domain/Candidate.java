package com.candidate_tracker.demo.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Candidate {
    private final String name;
    private final String email;
    private final String roleAppliedFor;
    private Stage stage = Stage.APPLIED;
    private String note = "";
    
    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


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
    
    public void addNote(String note) {
        String formattedDate = LocalDateTime.now().format(myFormatObj);
    	this.note += formattedDate + ": " + note + "\n\n";
    }
    
    public String getNote() {
    	return this.note;
    }
}