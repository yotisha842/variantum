package ru.variantum.worker.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload события variantum.export.completed, публикуемого backend-сервисом.
 * Поля должны соответствовать ru.variantum.event.ExportCompletedEvent в backend —
 * сервисы независимы и не делят общий jar, контракт — это сама Kafka-тема.
 */
public record ExportCompletedEvent(
        UUID projectId,
        UUID userId,
        String format,
        int sizeBytes,
        Instant occurredAt
) {}
