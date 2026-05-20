package com.corntrol.corntrol.domain.focus.repository;

import com.corntrol.corntrol.domain.focus.entity.CoolingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoolingQuestionRepository extends JpaRepository<CoolingQuestion, Long> {
    List<CoolingQuestion> findAllByRecordId(Long recordId);

    void deleteByUserId(Long userId);
}