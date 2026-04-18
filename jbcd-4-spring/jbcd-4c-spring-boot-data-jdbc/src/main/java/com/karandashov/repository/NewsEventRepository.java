package com.karandashov.repository;

import com.karandashov.entity.NewsEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsEventRepository extends CrudRepository<NewsEventEntity, UUID> {
}