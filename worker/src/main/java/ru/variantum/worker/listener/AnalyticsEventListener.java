package ru.variantum.worker.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.variantum.worker.domain.AnalysisEventRecord;
import ru.variantum.worker.domain.ExportEventRecord;
import ru.variantum.worker.event.DocumentAnalyzedEvent;
import ru.variantum.worker.event.ExportCompletedEvent;
import ru.variantum.worker.repository.AnalysisEventRepository;
import ru.variantum.worker.repository.ExportEventRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Consumer доменных событий, публикуемых основным backend-сервисом.
 * Каждое событие сохраняется в собственные таблицы (worker владеет своей частью схемы),
 * что и составляет границу ответственности этого микросервиса — аналитика экспорта и анализа,
 * не задевающая основной API.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AnalyticsEventListener {

    private final ExportEventRepository exportEventRepository;
    private final AnalysisEventRepository analysisEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "variantum.export.completed", groupId = "variantum-worker")
    public void onExportCompleted(String payload) {
        try {
            ExportCompletedEvent event = objectMapper.readValue(payload, ExportCompletedEvent.class);
            exportEventRepository.save(ExportEventRecord.builder()
                    .projectId(event.projectId())
                    .userId(event.userId())
                    .format(event.format())
                    .sizeBytes(event.sizeBytes())
                    .occurredAt(OffsetDateTime.ofInstant(event.occurredAt(), ZoneOffset.UTC))
                    .build());
            log.info("Сохранено событие export.completed: project={}, format={}, size={}B",
                    event.projectId(), event.format(), event.sizeBytes());
        } catch (Exception e) {
            log.error("Не удалось обработать событие export.completed: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "variantum.document.analyzed", groupId = "variantum-worker")
    public void onDocumentAnalyzed(String payload) {
        try {
            DocumentAnalyzedEvent event = objectMapper.readValue(payload, DocumentAnalyzedEvent.class);
            analysisEventRepository.save(AnalysisEventRecord.builder()
                    .subject(event.subject())
                    .textLength(event.textLength())
                    .occurredAt(OffsetDateTime.ofInstant(event.occurredAt(), ZoneOffset.UTC))
                    .build());
            log.info("Сохранено событие document.analyzed: subject={}, length={}",
                    event.subject(), event.textLength());
        } catch (Exception e) {
            log.error("Не удалось обработать событие document.analyzed: {}", e.getMessage(), e);
        }
    }
}
