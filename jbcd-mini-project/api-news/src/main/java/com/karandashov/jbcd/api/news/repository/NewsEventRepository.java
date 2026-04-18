package com.karandashov.jbcd.api.news.repository;

import com.karandashov.jbcd.api.news.entity.NewsEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsEventRepository extends CrudRepository<NewsEventEntity, UUID> {
}
