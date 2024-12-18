package com.example.taskManagement.service.impl;

import com.example.taskManagement.aspect.LogAfterReturning;
import com.example.taskManagement.aspect.LogAfterThrowing;
import com.example.taskManagement.aspect.LogAround;
import com.example.taskManagement.aspect.LogBefore;
import com.example.taskManagement.exeption.NotificationException;
import com.example.taskManagement.service.NotificationService;
import com.example.taskManagement.web.request.TaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @LogBefore
    @LogAfterThrowing
    @LogAfterReturning
    @LogAround
    @Override
    public void sendNotification(TaskRequest taskRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("notifications@example.com");
        message.setSubject("Task Status Updated");
        message.setText("Task ID: " + taskRequest.taskId() + " New Status: " + taskRequest.status());

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new NotificationException("Failed to send notification", e);
        }
    }
}
