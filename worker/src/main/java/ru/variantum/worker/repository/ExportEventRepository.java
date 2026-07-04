package ru.variantum.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.variantum.worker.domain.ExportEventRecord;

import java.util.List;
import java.util.UUID;

public interface ExportEventRepository extends JpaRepository<ExportEventRecord, UUID> {

    List<ExportEventRecord> findTop20ByOrderByReceivedAtDesc();

    long countByFormat(@Param("format") String format);
}
