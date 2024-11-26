package com.example.taskManagement.web.request;

public record NewTaskRequest(

        String title,

        String description,

        Long userId

) {
}
