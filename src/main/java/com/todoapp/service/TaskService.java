package com.todoapp.service;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public Task addTask(String title, User user) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        return repo.save(new Task(title.trim(), false, user));
    }
    

    public List<Task> getTasks(User user) {
        List<Task> tasks = repo.findByUser(user);
        tasks.sort(Comparator.comparing(Task::isCompleted));
        return tasks;
    }
    

    public boolean completeTask(Long id) {
        return repo.findById(id).map(task -> {
            task.setCompleted(true);
            repo.save(task);
            return true;
        }).orElse(false);
    }
    

    public boolean deleteTask(Long id) {
        if (!repo.existsById(id)) {
            return false;
        }
        repo.deleteById(id);
        return true;
    }
    
}
