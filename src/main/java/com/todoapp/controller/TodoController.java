package com.todoapp.controller;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TodoController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TodoController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/todos")
    public String getTodos(Model model, Authentication authentication) {
    
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
    
        model.addAttribute("todos", taskRepository.findByUser(user));
        return "todos";
    }
    
    @PostMapping("/todos")
    public String addTodo(@RequestParam String title,
                          Authentication authentication) {
    
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
    
        Task task = new Task(title, false, user);
        taskRepository.save(task);
    
        return "redirect:/todos";
    }
    

}
