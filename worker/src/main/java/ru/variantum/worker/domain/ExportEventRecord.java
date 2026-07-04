package ru.variantum.worker.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "worker_export_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportEventRecord {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 10)
    private String format;

    @Column(name = "size_bytes", nullable = false)
    private int sizeBytes;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "received_at", nullable = false, updatable = false)
    private OffsetDateTime receivedAt;

    @PrePersist
    void onCreate() {
        if (receivedAt == null) receivedAt = OffsetDateTime.now();
    }
}
