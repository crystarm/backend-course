package com.karandashov.service;

import com.karandashov.dto.ApprovedRequest;
import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsStatus;
import com.karandashov.temporal.NewsEventWorkflow;
import io.temporal.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsEventService {

    /**
     * В одну очередь отправляем и workflow, и activities этого учебного примера.
     */
    public static final String NEWS_TASK_QUEUE = "news-event-task-queue";

    private final WorkflowClient workflowClient;

    /**
     * Создаем новый workflow публикации новости.
     */
    public void create(CreateNewsEventRequest request) {
        UUID newsId = request.getId() == null ? UUID.randomUUID() : request.getId();
        NewsEvent event = new NewsEvent(newsId, request.getTitle(), request.getMessage(), NewsStatus.NEW);

        NewsEventWorkflow workflow = workflowClient.newWorkflowStub(
                NewsEventWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(NEWS_TASK_QUEUE)
                        .setWorkflowId(workflowId(newsId))
                        .build()
        );

        log.info("Starting Temporal workflow for newsId={}", newsId);
        WorkflowClient.start(workflow::process, event);
        log.info("Temporal workflow started for newsId={}", newsId);
    }

    /**
     * Читаем текущее состояние workflow через Temporal query.
     */
    public NewsEvent getById(UUID newsId) {
        NewsEventWorkflow workflow = workflowClient.newWorkflowStub(NewsEventWorkflow.class, workflowId(newsId));

        try {
            NewsEvent event = workflow.getCurrentState();
            log.info("Temporal query returned status={} for newsId={}", event.getStatus(), newsId);
            return event;
        } catch (WorkflowNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow for news was not found", ex);
        } catch (WorkflowException ex) {
            WorkflowStub workflowStub = WorkflowStub.fromTyped(workflow);
            try {
                NewsEvent result = workflowStub.getResult(1, TimeUnit.SECONDS, NewsEvent.class);
                log.info("Temporal result returned final status={} for newsId={}", result.getStatus(), newsId);
                return result;
            } catch (Exception resultEx) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to read news state from Temporal",
                        resultEx
                );
            }
        }
    }

    /**
     * Отправляем signal в workflow и не ждем завершения процесса.
     */
    public void process(ApprovedRequest request) {
        NewsEventWorkflow workflow = workflowClient.newWorkflowStub(
                NewsEventWorkflow.class,
                workflowId(request.getNewsId())
        );

        try {
            log.info("Sending Temporal signal {} for newsId={}", request.getStatus(), request.getNewsId());
            workflow.applyDecision(request.getStatus());
            log.info("Temporal signal {} has been sent for newsId={}", request.getStatus(), request.getNewsId());
        } catch (WorkflowNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow for news was not found", ex);
        } catch (WorkflowException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to process news in current workflow state",
                    ex
            );
        }
    }

    private String workflowId(UUID newsId) {
        return "news-event-" + newsId;
    }
}
