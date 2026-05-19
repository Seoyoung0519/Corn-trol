package com.corntrol.corntrol.domain.focus.repository;

import com.corntrol.corntrol.domain.focus.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    void deleteByUserId(Long userId);

    // 유저의 전체 몰입 시간(분) 합계 구하기 (결과가 null이면 0 반환)
    @Query("SELECT COALESCE(SUM(f.duration), 0) FROM FocusSession f WHERE f.userId = :userId")
    Long sumFocusTimeByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
