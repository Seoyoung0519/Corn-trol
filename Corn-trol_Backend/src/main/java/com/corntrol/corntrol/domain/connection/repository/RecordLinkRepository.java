package com.corntrol.corntrol.domain.connection.repository;

import com.corntrol.corntrol.domain.connection.entity.RecordLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecordLinkRepository extends JpaRepository<RecordLink, Long> {

    List<RecordLink> findByTopicAndIsConnectedTrue(String topic);

    Optional<RecordLink> findTopBySourceRecordIdAndIsConnectedFalseOrderByIdDesc(Long sourceRecordId);

    Optional<RecordLink> findBySourceRecordIdAndTargetRecordId(Long sourceRecordId, Long targetRecordId);

    @Query("SELECT r FROM RecordLink r WHERE (r.sourceRecordId = :recordId OR r.targetRecordId = :recordId) AND r.isConnected = true")
    List<RecordLink> findAllConnectedByRecordId(@Param("recordId") Long recordId);

    void deleteByUserId(Long userId);

    // 유저 아이디로 연결 노드 총 개수 세기
    Long countByUserId(Long userId);

    List<RecordLink> findByUserId(Long userId);
}