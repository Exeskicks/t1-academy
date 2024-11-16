package com.example.taskManagement.service;

import com.example.taskManagement.web.request.NewTaskRequest;
import com.example.taskManagement.web.respone.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse saveTask(NewTaskRequest taskResponse);

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, NewTaskRequest task);

    void deleteTask(Long id);

    List<TaskResponse> getAllTasks();


}
