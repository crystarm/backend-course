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
@Table(name = "news_table")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsEventEntity {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "title", nullable = false, length = 256)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;
}
