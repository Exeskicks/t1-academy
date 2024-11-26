package com.example.taskManagement.web;

import com.example.taskManagement.service.TaskService;
import com.example.taskManagement.web.request.NewTaskRequest;
import com.example.taskManagement.web.respone.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @RequestBody NewTaskRequest taskRequest) {
        return taskService.saveTask(taskRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse getTaskById(
            @PathVariable final Long id
    ) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse updateTaskById(
            @PathVariable final Long id,
            @RequestBody NewTaskRequest taskRequest
    ) {
        return taskService.updateTask(id, taskRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Void deleteTaskById(
            @PathVariable final Long id
    ) {
        taskService.deleteTask(id);
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }
}
