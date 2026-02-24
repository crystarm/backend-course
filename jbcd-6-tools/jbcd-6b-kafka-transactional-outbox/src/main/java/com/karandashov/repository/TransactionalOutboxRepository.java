package com.karandashov.repository;

import com.karandashov.entity.TransactionalOutboxEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface TransactionalOutboxRepository extends JpaRepository<TransactionalOutboxEntity, UUID> {

    Slice<TransactionalOutboxEntity> findByIsSendIsFalseOrderByCreatedDate(Pageable page);

    @Modifying
    @Query("update TransactionalOutboxEntity e set e.isSend = true where e.id in :ids")
    void markSent(@Param("ids") Collection<UUID> ids);
}
