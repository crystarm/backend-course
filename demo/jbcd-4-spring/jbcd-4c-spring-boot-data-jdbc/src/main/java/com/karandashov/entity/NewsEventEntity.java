package com.karandashov.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "news_table")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsEventEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("title")
    private String title;

    @Column("message")
    private String message;
}
