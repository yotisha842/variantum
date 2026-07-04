package ru.variantum.worker.event;

import java.time.Instant;

/** Payload события variantum.document.analyzed, публикуемого backend-сервисом. */
public record DocumentAnalyzedEvent(
        String subject,
        int textLength,
        Instant occurredAt
) {}
