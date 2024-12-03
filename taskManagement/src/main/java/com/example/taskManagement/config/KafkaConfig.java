package com.example.taskManagement.config;


import com.example.taskManagement.kafka.KafkaTaskProducer;
import com.example.taskManagement.kafka.MessageDeserializer;
import com.example.taskManagement.web.request.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("t1-demo")
    private String groupId;

    @Value("localhost:9092")
    private String servers;

    @Value("${t1.kafka.session.timeout.ms:150000}")
    private String sessionTimeout;

    @Value("${t1:kafka.max.partition.fetch.bytes:300000}")
    private String maxPartitionFetesBytes;

    @Value("${t1:kafka.max.poll.records:1}")
    private String maxPollRecords;

    @Value("${t1:kafka.max.poll.interval.ms:3000}")
    private String maxPollIntervalMs;

    @Value("t1_task_manager")
    private String taskManager;


    @Bean
    public ConsumerFactory<String, TaskRequest> consumerListenerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers); //СЕРВЕР
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); //консюмер группа
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //кто будет десериализовать ключ
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);  //кто будет десериализовать value
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.taskManagement.web.request.TaskRequest"); //что маппим. и где взять
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");//если маппим во что-то вне
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false); //заголовки


      /*  props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetesBytes);  //максимальный размер сообщения,
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords); //сколько сообщений прочитать
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs); //время сколько консьюмер может получать ответ от кафки*/

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //начинать с раннего сообщенря
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class); //для ошибок
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);//для ошибок

        DefaultKafkaConsumerFactory factory = new DefaultKafkaConsumerFactory<String, TaskRequest>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return factory;

    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TaskRequest> kafkaListenerContainerFactory(
            @Qualifier("consumerListenerFactory") ConsumerFactory<String, TaskRequest> consumerListenerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, TaskRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerListenerFactory);
        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setCommonErrorHandler(errorHandler());
    }

    //для повторной попытки почитать сообщения
    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(IllegalAccessException.class);
        handler.setRetryListeners(((record, ex, deliveryAttempt) -> {
            log.error(" RetryListeners message = {}, offset = {} deliveryAttempt",
                    ex.getMessage(),
                    record.offset(),
                    deliveryAttempt);
        }));
        return handler;
    }

    @Bean("task")
    public KafkaTemplate<String, TaskRequest> kafkaTemplate(ProducerFactory<String, TaskRequest> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "t1.kafka.prod",
            havingValue = "true",
            matchIfMissing = true)
    public KafkaTaskProducer producerClient(@Qualifier("task") KafkaTemplate template) {
        template.setDefaultTopic(taskManager);
        return new KafkaTaskProducer(template);
    }


    @Bean
    public ProducerFactory<String, TaskRequest> producerClientFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }
}


