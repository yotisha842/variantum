package ru.variantum.worker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.variantum.worker.domain.AnalysisEventRecord;
import ru.variantum.worker.domain.ExportEventRecord;
import ru.variantum.worker.repository.AnalysisEventRepository;
import ru.variantum.worker.repository.ExportEventRepository;

import java.util.List;

/** Показывает, что worker реально принимает и хранит события — для проверки и демо. */
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ExportEventRepository exportEventRepository;
    private final AnalysisEventRepository analysisEventRepository;

    @GetMapping("/exports")
    public StatsResponse<ExportEventRecord> exports() {
        return new StatsResponse<>(exportEventRepository.count(), exportEventRepository.findTop20ByOrderByReceivedAtDesc());
    }

    @GetMapping("/analyses")
    public StatsResponse<AnalysisEventRecord> analyses() {
        return new StatsResponse<>(analysisEventRepository.count(), analysisEventRepository.findTop20ByOrderByReceivedAtDesc());
    }

    public record StatsResponse<T>(long total, List<T> recent) {}
}
