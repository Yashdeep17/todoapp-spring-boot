package com.todoapp.controller;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TodoController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TodoController(TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/todos")
public String getTodos(
        @RequestParam(required = false) String filter,
        Model model,
        Authentication authentication) {

    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<Task> todos;

    if ("completed".equals(filter)) {
        todos = taskRepository.findByUserAndCompletedOrderByCreatedAtDesc(user, true);
    } else if ("pending".equals(filter)) {
        todos = taskRepository.findByUserAndCompletedOrderByCreatedAtDesc(user, false);
    } else {
        todos = taskRepository.findByUserOrderByCreatedAtDesc(user);
        filter = "all";
    }

    model.addAttribute("todos", todos);
    model.addAttribute("filter", filter);

    return "todos";
}




@PostMapping("/todos")
public String addTodo(@RequestParam String title,
                      @RequestParam(required = false) String filter,
                      Authentication authentication) {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    Task task = new Task(title, false, user);
    taskRepository.save(task);

    // 🔥 Preserve filter after adding
    if (filter != null && !filter.isEmpty()) {
        return "redirect:/todos?filter=" + filter;
    }

    return "redirect:/todos";
}


@PostMapping("/todos/toggle")
public String toggleTask(@RequestParam Long taskId,
                         @RequestParam(required = false) String filter,
                         Authentication authentication) {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    Task task = taskRepository.findById(taskId).orElseThrow();

    if (!task.getUser().getId().equals(user.getId())) {
        return "redirect:/todos";
    }

    task.setCompleted(!task.isCompleted());
    taskRepository.save(task);

    // 🔥 Preserve filter
    if (filter != null && !filter.isEmpty()) {
        return "redirect:/todos?filter=" + filter;
    }

    return "redirect:/todos";
}

@PostMapping("/todos/delete")
public String deleteTodo(@RequestParam Long taskId,
                         @RequestParam(required = false) String filter,
                         Authentication authentication) {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    Task task = taskRepository.findById(taskId).orElseThrow();

    // 🔒 Security check: only owner can delete
    if (!task.getUser().getId().equals(user.getId())) {
        return "redirect:/todos";
    }

    taskRepository.delete(task);

    // 🔥 Preserve filter
    if (filter != null && !filter.isEmpty()) {
        return "redirect:/todos?filter=" + filter;
    }

    return "redirect:/todos";
}


}
