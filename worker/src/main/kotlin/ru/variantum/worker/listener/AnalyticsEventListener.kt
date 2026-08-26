package ru.variantum.worker.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.variantum.worker.domain.AnalysisEventRecord
import ru.variantum.worker.domain.ExportEventRecord
import ru.variantum.worker.event.DocumentAnalyzedEvent
import ru.variantum.worker.event.ExportCompletedEvent
import ru.variantum.worker.repository.AnalysisEventRepository
import ru.variantum.worker.repository.ExportEventRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Consumer доменных событий, публикуемых основным backend-сервисом.
 * Каждое событие сохраняется в собственные таблицы (worker владеет своей частью схемы),
 * что и составляет границу ответственности этого микросервиса — аналитика экспорта и анализа,
 * не задевающая основной API.
 */
@Component
class AnalyticsEventListener(
    private val exportEventRepository: ExportEventRepository,
    private val analysisEventRepository: AnalysisEventRepository,
    private val objectMapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(AnalyticsEventListener::class.java)

    @KafkaListener(topics = ["variantum.export.completed"], groupId = "variantum-worker")
    fun onExportCompleted(payload: String) {
        try {
            val event = objectMapper.readValue(payload, ExportCompletedEvent::class.java)
            exportEventRepository.save(
                ExportEventRecord(
                    projectId = event.projectId,
                    userId = event.userId,
                    format = event.format,
                    sizeBytes = event.sizeBytes,
                    occurredAt = OffsetDateTime.ofInstant(event.occurredAt, ZoneOffset.UTC),
                )
            )
            log.info(
                "Сохранено событие export.completed: project={}, format={}, size={}B",
                event.projectId, event.format, event.sizeBytes,
            )
        } catch (e: Exception) {
            log.error("Не удалось обработать событие export.completed: {}", e.message, e)
        }
    }

    @KafkaListener(topics = ["variantum.document.analyzed"], groupId = "variantum-worker")
    fun onDocumentAnalyzed(payload: String) {
        try {
            val event = objectMapper.readValue(payload, DocumentAnalyzedEvent::class.java)
            analysisEventRepository.save(
                AnalysisEventRecord(
                    subject = event.subject,
                    textLength = event.textLength,
                    occurredAt = OffsetDateTime.ofInstant(event.occurredAt, ZoneOffset.UTC),
                )
            )
            log.info(
                "Сохранено событие document.analyzed: subject={}, length={}",
                event.subject, event.textLength,
            )
        } catch (e: Exception) {
            log.error("Не удалось обработать событие document.analyzed: {}", e.message, e)
        }
    }
}
