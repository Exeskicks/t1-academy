package com.example.taskManagement.service.impl;

import com.example.taskManagement.aspect.LogAfterReturning;
import com.example.taskManagement.aspect.LogAfterThrowing;
import com.example.taskManagement.aspect.LogAround;
import com.example.taskManagement.aspect.LogBefore;
import com.example.taskManagement.common.TaskStatus;
import com.example.taskManagement.entity.Task;
import com.example.taskManagement.kafka.KafkaTaskProducer;
import com.example.taskManagement.repository.TaskRepository;
import com.example.taskManagement.service.TaskService;
import com.example.taskManagement.web.request.TaskRequest;
import com.example.taskManagement.web.respone.TaskResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final KafkaTaskProducer kafkaTaskProducer;

    @LogBefore
    @LogAfterThrowing
    @Override
    public TaskResponse saveTask(TaskRequest taskResponse) {

        Task task = new Task();

        task.setTitle(taskResponse.title());
        task.setDescription(taskResponse.description());
        task.setStatus(TaskStatus.valueOf(taskResponse.status()));
        task.setUserId(taskResponse.userId());
        log.info(taskResponse.toString());
        task = taskRepository.save(task);

        TaskResponse taskResponse1 = new TaskResponse(task.getId(), task.getTitle(), task.getStatus().toString(), task.getDescription());

        return taskResponse1;
    }

    @LogBefore
    @LogAfterReturning
    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException
                        ("Task with id " + id + " not found"));

        TaskResponse taskResponse1 = new TaskResponse(task.getId(), task.getTitle(), task.getStatus().toString(), task.getDescription());
        return taskResponse1;
    }


    @LogBefore
    @LogAfterThrowing
    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException
                        ("Task with id " + id + " not found"));

        taskRepository.delete(task);
    }

    @LogBefore
    @LogAfterReturning
    @Override
    public TaskResponse updateTask(Long id, TaskRequest updateTaskRequest) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException
                        ("Task with id " + id + " not found"));

        task.setTitle(updateTaskRequest.title());
        task.setDescription(updateTaskRequest.description());
        task.setUserId(updateTaskRequest.userId());
        task = taskRepository.save(task);


        TaskResponse taskResponse1 = new TaskResponse(task.getId(), task.getTitle(), task.getStatus().toString(), task.getDescription());
        return taskResponse1;
    }

    @LogBefore
    @LogAfterReturning
    @Override
    public TaskResponse updateStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        // Update the task status
        task.setStatus(TaskStatus.valueOf(newStatus.toString()));
        task = taskRepository.save(task);

        // Send the update to Kafka
        kafkaTaskProducer.sendStatusUpdate(task.getId(), newStatus);


        TaskResponse taskResponse1 = new TaskResponse(task.getId(), task.getTitle(), task.getStatus().toString(), task.getDescription());
        return taskResponse1;
    }


    @LogBefore
    @LogAfterReturning
    @LogAround
    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse(task.getId(), task.getTitle(), task.getStatus().toString(), task.getDescription());
        return taskResponse;
    }
}
