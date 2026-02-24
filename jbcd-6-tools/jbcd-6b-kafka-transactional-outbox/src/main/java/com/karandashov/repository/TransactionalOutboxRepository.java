package com.karandashov.repository;

import com.karandashov.entity.TransactionalOutboxEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionalOutboxRepository extends JpaRepository<TransactionalOutboxEntity, UUID> {

    Slice<TransactionalOutboxEntity> findByIsSendIsFalseOrderByCreatedDate(Pageable page);
}
