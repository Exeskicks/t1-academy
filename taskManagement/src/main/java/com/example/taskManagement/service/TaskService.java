package com.example.taskManagement.service;

import com.example.taskManagement.common.TaskStatus;
import com.example.taskManagement.web.request.TaskRequest;
import com.example.taskManagement.web.respone.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse saveTask(TaskRequest taskResponse);

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, TaskRequest task);

    TaskResponse updateStatus(Long id, TaskStatus status);

    void deleteTask(Long id);

    List<TaskResponse> getAllTasks();


}
