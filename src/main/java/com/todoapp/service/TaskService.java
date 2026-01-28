package com.todoapp.service;

import com.todoapp.model.Task;
import com.todoapp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public void addTask(String title) {
        repo.save(new Task(title, false));
    }

    public List<Task> getTasks() {
        return repo.findAll();
    }

    public void completeTask(int id) {
        Task t = repo.findById(id).orElseThrow();
        t.setStatus(true);
        repo.save(t);
    }

    public void deleteTask(int id) {
        repo.deleteById(id);
    }
}
