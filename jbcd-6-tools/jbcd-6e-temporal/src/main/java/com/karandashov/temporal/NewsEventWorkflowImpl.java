package com.karandashov.temporal;

import com.karandashov.dto.ApprovedRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsStatus;
import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

// Реализация workflow регистрируется в worker'е и исполняется самим Temporal.
@WorkflowImpl(taskQueues = "news-event-task-queue")
public class NewsEventWorkflowImpl implements NewsEventWorkflow {

    private static final Logger log = Workflow.getLogger(NewsEventWorkflowImpl.class);

    // Через activity stub workflow вызывает внешний код, который может выполнять I/O.
    private final NewsActivities activities = Workflow.newActivityStub(
            NewsActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .build()
    );

    private NewsEvent currentState;
    private ApprovedRequest.Status decision;

    @Override
    public NewsEvent process(NewsEvent event) {
        currentState = new NewsEvent(event.getId(), event.getTitle(), event.getMessage(), NewsStatus.NEW);
        log.info("Workflow started for newsId={}", event.getId());
        log.info("Workflow state set to NEW for newsId={}", event.getId());

        log.info("Starting title validation for newsId={}", event.getId());
        if (!activities.validateTitle(event.getTitle())) {
            log.info("Title validation failed for newsId={}, moving to TRASH", event.getId());
            currentState = activities.finalizeNews(currentState, NewsStatus.TRASH);
            log.info("Workflow finished for newsId={} with status={}", event.getId(), currentState.getStatus());
            return currentState;
        }

        log.info("Starting message validation for newsId={}", event.getId());
        if (!activities.validateMessage(event.getMessage())) {
            log.info("Message validation failed for newsId={}, moving to TRASH", event.getId());
            currentState = activities.finalizeNews(currentState, NewsStatus.TRASH);
            log.info("Workflow finished for newsId={} with status={}", event.getId(), currentState.getStatus());
            return currentState;
        }

        log.info("Validation finished successfully for newsId={}, waiting for approval signal", event.getId());
        // Workflow.await безопасно "усыпляет" workflow до прихода signal.
        Workflow.await(() -> decision != null);
        log.info("Approval signal received for newsId={}, decision={}", event.getId(), decision);

        if (decision == ApprovedRequest.Status.APPROVED) {
            currentState = activities.finalizeNews(currentState, NewsStatus.APPROVED);
            log.info("Workflow finished for newsId={} with status=APPROVED", event.getId());
            return currentState;
        }

        currentState = activities.finalizeNews(currentState, NewsStatus.DECLINED);
        log.info("Workflow finished for newsId={} with status=DECLINED", event.getId());
        return currentState;
    }

    @Override
    public void applyDecision(ApprovedRequest.Status status) {
        log.info("Signal applyDecision received for newsId={}, decision={}",
                currentState == null ? null : currentState.getId(), status);
        this.decision = status;
    }

    @Override
    public NewsEvent getCurrentState() {
        log.info("Query getCurrentState called for newsId={}, status={}",
                currentState == null ? null : currentState.getId(),
                currentState == null ? null : currentState.getStatus());
        return currentState;
    }
}
