package ru.variantum.event;

/** Имена Kafka-топиков, общие для producer'а (backend) и consumer'а (variantum-worker). */
public final class KafkaTopics {

    public static final String EXPORT_COMPLETED = "variantum.export.completed";
    public static final String DOCUMENT_ANALYZED = "variantum.document.analyzed";

    private KafkaTopics() {}
}
