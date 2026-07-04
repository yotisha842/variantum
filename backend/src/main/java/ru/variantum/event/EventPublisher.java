package ru.variantum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.variantum.config.AppProperties;

import java.time.Instant;
import java.util.UUID;

/**
 * Публикует доменные события в Kafka для variantum-worker (сервис аналитики).
 * Публикация не блокирует основной поток запроса и не должна ронять его:
 * любая ошибка (Kafka недоступна, сериализация и т.п.) только логируется.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppProperties appProperties;

    public void publishExportCompleted(UUID projectId, UUID userId, String format, int sizeBytes) {
        if (!appProperties.kafka().enabled()) return;
        try {
            var event = new ExportCompletedEvent(projectId, userId, format, sizeBytes, Instant.now());
            kafkaTemplate.send(KafkaTopics.EXPORT_COMPLETED, projectId.toString(), event)
                    .exceptionally(ex -> {
                        log.warn("Не удалось отправить событие export.completed в Kafka: {}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.warn("Ошибка публикации события export.completed: {}", e.getMessage());
        }
    }

    public void publishDocumentAnalyzed(String subject, int textLength) {
        if (!appProperties.kafka().enabled()) return;
        try {
            var event = new DocumentAnalyzedEvent(subject, textLength, Instant.now());
            kafkaTemplate.send(KafkaTopics.DOCUMENT_ANALYZED, event)
                    .exceptionally(ex -> {
                        log.warn("Не удалось отправить событие document.analyzed в Kafka: {}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.warn("Ошибка публикации события document.analyzed: {}", e.getMessage());
        }
    }
}
