package com.example.taskManagement.service;

import com.example.taskManagement.web.request.TaskRequest;

public interface NotificationService {

    void sendNotification(TaskRequest taskRequest);
}
