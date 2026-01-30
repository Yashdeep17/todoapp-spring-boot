package com.todoapp.repository;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 🔥 VERY IMPORTANT
    List<Task> findByUser(User user);


List<Task> findByUserAndCompleted(User user, boolean completed);

List<Task> findByUserOrderByCreatedAtDesc(User user);

    List<Task> findByUserAndCompletedOrderByCreatedAtDesc(User user, boolean completed);

}
