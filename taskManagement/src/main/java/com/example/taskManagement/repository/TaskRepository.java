package com.example.taskManagement.repository;

import com.example.taskManagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
