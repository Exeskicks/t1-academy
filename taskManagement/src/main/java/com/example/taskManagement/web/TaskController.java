package com.example.taskManagement.web;

import com.example.taskManagement.common.TaskStatus;
import com.example.taskManagement.kafka.KafkaTaskProducer;
import com.example.taskManagement.service.TaskService;
import com.example.taskManagement.web.request.TaskRequest;
import com.example.taskManagement.web.respone.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Value("t1_demo_task_updates")
    private String taskUpdateTopic;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @RequestBody TaskRequest taskRequest) {
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
            @RequestBody TaskRequest taskRequest
    ) {
        return taskService.updateTask(id, taskRequest);
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public void updateTaskStatus(@PathVariable Long id, @RequestBody TaskStatus newStatus) {
        taskService.updateStatus(id, newStatus);
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
