package com.todoapp.controller;

import com.todoapp.model.Task;
import com.todoapp.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> getTasks() {
        return service.getTasks();
    }

    @PostMapping
    public void addTask(@RequestParam String title) {
        service.addTask(title);
    }

    @PutMapping("/{id}")
    public void complete(@PathVariable int id) {
        service.completeTask(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.deleteTask(id);
    }
}
