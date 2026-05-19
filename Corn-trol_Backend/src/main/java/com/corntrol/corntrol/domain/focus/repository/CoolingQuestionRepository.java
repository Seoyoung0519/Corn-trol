package com.corntrol.corntrol.domain.focus.repository;

import com.corntrol.corntrol.domain.focus.entity.CoolingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoolingQuestionRepository extends JpaRepository<CoolingQuestion, Long> {
    // 나중에 질문 조회 API (GET /focus/questions/{recordId}) 만들 때 사용할 메서드
    List<CoolingQuestion> findAllByRecordId(Long recordId);

    void deleteByUserId(Long userId);
}