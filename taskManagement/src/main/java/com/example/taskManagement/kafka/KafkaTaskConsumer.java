package com.example.taskManagement.kafka;

import com.example.taskManagement.service.NotificationService;
import com.example.taskManagement.web.request.TaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskConsumer {

    private final NotificationService notificationService;


    @KafkaListener(topics = "t1_demo_task_updates", groupId = "task-group")
    public void listen(TaskRequest taskRequest) {
        log.info("Received task update: {}", taskRequest);
        notificationService.sendNotification(taskRequest);
    }
}