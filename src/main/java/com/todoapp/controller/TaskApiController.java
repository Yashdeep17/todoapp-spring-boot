package com.todoapp.controller;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskApiController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // toggle done/undo
    @PutMapping("/{id}/toggle")
    public void toggleTask(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        Task task = taskRepository.findById(id).orElseThrow();
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }

    // delete task
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        Task task = taskRepository.findById(id).orElseThrow();
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        taskRepository.deleteById(id);
    }

    // ======================
// ADD TASK (AJAX)
// ======================
@PostMapping
public Task addTask(@RequestBody Task task, Authentication authentication) {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    task.setUser(user);
    task.setCompleted(false);
    task.setStatus("TODO");

    return taskRepository.save(task);
}

@PutMapping("/{id}")
public Task updateTask(@PathVariable Long id,
                       @RequestParam String title,
                       Authentication authentication) {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    Task task = taskRepository.findById(id).orElseThrow();

    if (!task.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Unauthorized");
    }

    task.setTitle(title);

    return taskRepository.save(task);
}

@PutMapping("/{id}/priority")
public Task updatePriority(@PathVariable Long id,
                           @RequestParam String priority,
                           Authentication authentication) {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    Task task = taskRepository.findById(id).orElseThrow();

    if (!task.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Unauthorized");
    }

    task.setPriority(priority);

    return taskRepository.save(task);
}


}
