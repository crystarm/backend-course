package com.karandashov.repository;

import com.karandashov.entity.NewsEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsEventRepository extends JpaRepository<NewsEventEntity, UUID> {
}
