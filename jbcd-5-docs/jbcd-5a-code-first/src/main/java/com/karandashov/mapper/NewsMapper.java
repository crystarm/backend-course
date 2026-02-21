package com.karandashov.mapper;

import com.karandashov.dto.NewsEvent;
import com.karandashov.entity.NewsEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {

    NewsEvent toDto(NewsEventEntity entity);

    List<NewsEvent> toDtoList(List<NewsEventEntity> entityList);
}
