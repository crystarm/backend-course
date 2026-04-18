package com.karandashov.jbcd.api.captcha.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NewsEventEntity entity)) return false;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
