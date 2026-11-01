package com.candidate_tracker.demo.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Candidate {

    private Long id;
    private final String name;
    private final String email;
    private final String roleAppliedFor;
    private String phone;
    private String source;
    private String resumeText;
    
    private Stage stage = Stage.APPLIED;
    private final List<Note> notes = new ArrayList<>();
    private Instant createdAt = Instant.now();
    
    public Candidate(String name, String email, String roleAppliedFor, String phone, String source, String resumeText) {
        this.name = name;
        this.email = email;
        this.roleAppliedFor = roleAppliedFor;
        this.phone = phone;
        this.source = source;
        this.resumeText = resumeText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleAppliedFor() {
        return roleAppliedFor;
    }

    public Stage getStage() {
        return stage;
    }
    
    public Instant getCreatedAt() {
    	return createdAt;
    }
    
    public void setCreatedAt(Instant time) {
    	this.createdAt = time;
    }

    public String getPhone() {
		return phone;
	}

	public String getSource() {
		return source;
	}

	public String getResumeText() {
		return resumeText;
	}

	public void moveTo(Stage newStage) {
        this.stage = Objects.requireNonNull(newStage);
    }

    public void addNote(String text, Instant createdAt) {
        notes.add(new Note(text, createdAt));
    }

    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }
}