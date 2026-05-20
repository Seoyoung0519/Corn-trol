package com.corntrol.corntrol.domain.report.repository;

import com.corntrol.corntrol.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserId(Long userId);
}