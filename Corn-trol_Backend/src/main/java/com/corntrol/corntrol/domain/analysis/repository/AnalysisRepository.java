package com.corntrol.corntrol.domain.analysis.repository;

import com.corntrol.corntrol.domain.analysis.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    Optional<Analysis> findTopByRecordIdOrderByIdDesc(Long recordId);

    void deleteByUserId(String userId);
}