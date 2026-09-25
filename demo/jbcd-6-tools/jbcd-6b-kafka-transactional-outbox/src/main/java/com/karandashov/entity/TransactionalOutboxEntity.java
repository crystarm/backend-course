package com.karandashov.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "transactional_outbox")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionalOutboxEntity {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "topic", nullable = false, length = 256)
    private String topic;

    @Column(name = "key", nullable = false, length = 256)
    private String key;

    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;
}
