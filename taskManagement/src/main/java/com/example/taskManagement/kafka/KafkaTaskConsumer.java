package com.example.taskManagement.kafka;

import com.example.taskManagement.aspect.LogAfterReturning;
import com.example.taskManagement.aspect.LogAfterThrowing;
import com.example.taskManagement.aspect.LogAround;
import com.example.taskManagement.aspect.LogBefore;
import com.example.taskManagement.exeption.NotificationException;
import com.example.taskManagement.service.NotificationService;
import com.example.taskManagement.web.request.TaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskConsumer {

    private final NotificationService notificationService;

    @LogBefore
    @LogAfterThrowing
    @LogAfterReturning
    @LogAround
    @KafkaListener(id = "${spring.kafka.group-id}", topics = "${spring.kafka.topic}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<TaskRequest> messageList, Acknowledgment ack) {
        try {
            for (TaskRequest message : messageList) {
                try {
                    notificationService.sendNotification(message);
                } catch (NotificationException e) {
                    log.error("Failed to send notification for task update: {}", message, e);
                }
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing task updates", e);
        }
    }
}