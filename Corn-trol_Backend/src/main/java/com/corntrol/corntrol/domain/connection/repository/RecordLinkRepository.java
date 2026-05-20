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

    // 특정 기록이 이미 다른 네트워크에 연결되어 있는지 확인 (isConnected = true 인 것만)
    boolean existsBySourceRecordIdOrTargetRecordIdAndIsConnectedTrue(Long sourceId, Long targetId);

    // 두 기록 간에 이미 연결된 링크가 있는지 양방향으로 확인
    @Query("SELECT COUNT(r) > 0 FROM RecordLink r " +
            "WHERE ((r.sourceRecordId = :a AND r.targetRecordId = :b) " +
            "OR (r.sourceRecordId = :b AND r.targetRecordId = :a)) " +
            "AND r.isConnected = true")
    boolean existsConnectedLinkBetween(@Param("a") Long a, @Param("b") Long b);
}