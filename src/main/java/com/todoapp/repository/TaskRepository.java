package com.todoapp.repository;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByUser(User user);

}
