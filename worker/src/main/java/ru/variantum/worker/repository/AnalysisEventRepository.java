package ru.variantum.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.variantum.worker.domain.AnalysisEventRecord;

import java.util.List;
import java.util.UUID;

public interface AnalysisEventRepository extends JpaRepository<AnalysisEventRecord, UUID> {

    List<AnalysisEventRecord> findTop20ByOrderByReceivedAtDesc();
}
