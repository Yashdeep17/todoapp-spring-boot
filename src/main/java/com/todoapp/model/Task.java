package com.todoapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private boolean status;

    public Task() {}

    public Task(String title, boolean status) {
        this.title = title;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isStatus() { return status; }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
