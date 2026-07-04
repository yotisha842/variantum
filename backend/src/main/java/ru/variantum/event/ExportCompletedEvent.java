package ru.variantum.event;

import java.time.Instant;
import java.util.UUID;

/** Публикуется после успешной генерации PDF/DOCX. Потребляется variantum-worker для статистики. */
public record ExportCompletedEvent(
        UUID projectId,
        UUID userId,
        String format,
        int sizeBytes,
        Instant occurredAt
) {}
