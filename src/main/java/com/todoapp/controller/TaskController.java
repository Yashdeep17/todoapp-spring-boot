package com.todoapp.controller;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.UserRepository;
import com.todoapp.service.TaskService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;
    private final UserRepository userRepository;

    public TaskController(TaskService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping
public ResponseEntity<List<Task>> getTasks(Authentication authentication) {
    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();
    return ResponseEntity.ok(service.getTasks(user));
}


    @PostMapping
public ResponseEntity<?> addTask(@RequestParam String title, Authentication authentication) {
    try {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Task task = service.addTask(title, user);
        return ResponseEntity.status(201).body(task);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


@PutMapping("/{id}")
public ResponseEntity<?> complete(@PathVariable Long id) {
    boolean updated = service.completeTask(id);
    if (!updated) {
        return ResponseEntity.status(404).body("Task not found");
    }
    return ResponseEntity.ok("Task marked as completed");
}


@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable Long id) {
    boolean deleted = service.deleteTask(id);
    if (!deleted) {
        return ResponseEntity.status(404).body("Task not found");
    }
    return ResponseEntity.ok("Task deleted");
}

}
