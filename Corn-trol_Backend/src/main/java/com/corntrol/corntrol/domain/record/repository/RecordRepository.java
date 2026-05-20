package com.corntrol.corntrol.domain.record.repository;

import com.corntrol.corntrol.domain.record.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    Optional<Record> findByIdAndUser_Id(Long id, Long userId);

    @Query("SELECT r FROM Record r " +
            "JOIN RecordLink rl ON r.id = rl.targetRecordId " +
            "WHERE rl.sourceRecordId = :recordId " +
            "AND rl.isConnected = true " +
            "AND r.user.id = :userId")
    List<Record> findAllConnectedRecords(@Param("recordId") Long recordId, @Param("userId") Long userId);

    Page<Record> findAllByUser_Id(Long userId, Pageable pageable);

    @Query("SELECT r FROM Record r WHERE r.user.id = :userId AND " +
            "(r.content LIKE %:keyword% OR r.mainTopic LIKE %:keyword% OR r.keywords LIKE %:keyword%)")
    Page<Record> searchRecords(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    void deleteAllByUserId(Long userId);

    List<Record> findByUser_Id(Long userId);

    Page<Record> findAllByUser_IdAndCreatedAtBetween(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end, Pageable pageable);
}