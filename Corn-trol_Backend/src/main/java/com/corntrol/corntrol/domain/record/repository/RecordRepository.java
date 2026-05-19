package com.corntrol.corntrol.domain.record.repository;

import com.corntrol.corntrol.domain.record.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    // 현재 기록과 연결된 모든 타겟 기록들을 가져오는 쿼리
    @Query("SELECT r FROM Record r " +
            "JOIN RecordLink rl ON r.id = rl.targetRecordId " +
            "WHERE rl.sourceRecordId = :recordId AND rl.isConnected = true")
    List<Record> findAllConnectedRecords(@Param("recordId") Long recordId);

    Page<Record> findAllByUser_Id(Long userId, Pageable pageable);

    @Query("SELECT r FROM Record r WHERE r.user.id = :userId AND " +
            "(r.content LIKE %:keyword% OR r.mainTopic LIKE %:keyword% OR r.keywords LIKE %:keyword%)")
    Page<Record> searchRecords(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    // 유저 ID로 관련된 모든 기록 삭제
    void deleteAllByUserId(Long userId);

    List<Record> findByUser_Id(Long userId);

    Page<Record> findAllByUser_IdAndCreatedAtBetween(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end, Pageable pageable);
}
