package com.example.taskManagement.web.respone;

public record TaskResponse(

    Long id,

    String title,

    String status,

    String description

){

}
