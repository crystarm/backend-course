package com.karandashov.temporal;

import com.karandashov.dto.ApprovedRequest;
import com.karandashov.dto.NewsEvent;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

// Контракт workflow, который Temporal будет запускать и восстанавливать из истории.
@WorkflowInterface
public interface NewsEventWorkflow {

    // Основной вход в workflow.
    @WorkflowMethod
    NewsEvent process(NewsEvent event);

    // Signal позволяет асинхронно отправить решение в уже работающий workflow.
    @SignalMethod
    void applyDecision(ApprovedRequest.Status status);

    // Query возвращает текущее состояние workflow без изменения его истории.
    @QueryMethod
    NewsEvent getCurrentState();
}
