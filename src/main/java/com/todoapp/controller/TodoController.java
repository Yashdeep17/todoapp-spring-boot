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
    User user = userRepository.findByUsername(username).orElseThrow();

    model.addAttribute("username", username);


    List<Task> tasks;

    if ("pending".equals(filter)) {
        tasks = taskRepository.findByUser(user).stream()
                .filter(task -> !task.isCompleted())
                .toList();
    } else if ("completed".equals(filter)) {
        tasks = taskRepository.findByUser(user).stream()
                .filter(Task::isCompleted)
                .toList();
    } else {
        tasks = taskRepository.findByUser(user);
    }

    List<Task> allUserTasks = taskRepository.findByUser(user);
    long total = allUserTasks.size();
    long completed = allUserTasks.stream().mapToLong(task -> task.isCompleted() ? 1 : 0).sum();
    long pending = total - completed;

    model.addAttribute("todos", tasks);
    model.addAttribute("filter", filter);

    // ⭐ add counts
    model.addAttribute("totalCount", total);
    model.addAttribute("pendingCount", pending);
    model.addAttribute("completedCount", completed);

    return "todos";
}



@PostMapping("/todos")
public String addTodo(
        @RequestParam String title,
        @RequestParam String priority,
        Authentication authentication) {

    String username = authentication.getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    Task task = new Task();
    task.setTitle(title);
    task.setCompleted(false);
    task.setPriority(priority);

    task.setUser(user);   // ⭐⭐⭐ MOST IMPORTANT LINE

    taskRepository.save(task);

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
