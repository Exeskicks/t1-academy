package com.example.taskManagement.web.request;

public record TaskRequest(

        Long taskId,

        String title,

        String description,

        String status,

        Long userId

) {
}
