package ru.variantum.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import ru.variantum.event.KafkaTopics;

/**
 * Автосоздание топиков при старте (однонодовый брокер в docker-compose).
 * Активно только если app.kafka.enabled=true — не мешает работе без Kafka.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTopicConfig {

    @Bean
    public NewTopic exportCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.EXPORT_COMPLETED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic documentAnalyzedTopic() {
        return TopicBuilder.name(KafkaTopics.DOCUMENT_ANALYZED)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
