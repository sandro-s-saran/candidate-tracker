package com.candidate_tracker.demo.domain;

import java.time.Instant;
import java.util.ArrayList;


public class Candidate {
    private final String name;
    private final String email;
    private final String roleAppliedFor;
    private Stage stage = Stage.APPLIED;
    private ArrayList<Note> notes = new ArrayList<>();
    

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
    
    public void addNote(String note, Instant time) {
        notes.add(new Note(note, time));
    }
    
    public ArrayList<Note> getNotes() {
    	return this.notes;
    }
}