package com.example.taskManagement.kafka;

import com.example.taskManagement.common.TaskStatus;
import com.example.taskManagement.web.request.TaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskProducer {

    private final KafkaTemplate<String, TaskRequest> kafkaTemplate;


    public void sendStatusUpdate(Long taskId, TaskStatus newStatus) {
        TaskRequest request = new TaskRequest(taskId, null, null, newStatus.toString(), null);
        kafkaTemplate.send("t1_demo_task_updates", request);
    }
}
