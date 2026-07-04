package ru.variantum.event;

import java.time.Instant;

/** Публикуется после успешного анализа эталонного задания через GigaChat. */
public record DocumentAnalyzedEvent(
        String subject,
        int textLength,
        Instant occurredAt
) {}
