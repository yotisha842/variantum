-- IF NOT EXISTS: расширение может быть уже создано backend-сервисом (общая БД)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE worker_export_events (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id   UUID NOT NULL,
    user_id      UUID NOT NULL,
    format       VARCHAR(10) NOT NULL,
    size_bytes   INTEGER NOT NULL,
    occurred_at  TIMESTAMPTZ NOT NULL,
    received_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_worker_export_events_project_id ON worker_export_events (project_id);

CREATE TABLE worker_analysis_events (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subject      VARCHAR(100) NOT NULL,
    text_length  INTEGER NOT NULL,
    occurred_at  TIMESTAMPTZ NOT NULL,
    received_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
