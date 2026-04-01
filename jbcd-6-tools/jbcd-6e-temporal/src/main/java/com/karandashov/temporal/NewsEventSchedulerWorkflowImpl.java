package com.karandashov.temporal;

import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsStatus;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

// Workflow, который стартует по расписанию и порождает обычный процесс публикации новости.
@WorkflowImpl(taskQueues = "news-event-task-queue")
public class NewsEventSchedulerWorkflowImpl implements NewsEventSchedulerWorkflow {

    private static final Logger log = Workflow.getLogger(NewsEventSchedulerWorkflowImpl.class);

    @Override
    public void publishMinuteGreeting() {
        log.info("Scheduled workflow started. workflowId={}", Workflow.getInfo().getWorkflowId());

        NewsEventWorkflow childWorkflow = Workflow.newChildWorkflowStub(
                NewsEventWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                        .setTaskQueue("news-event-task-queue")
                        .setWorkflowId("scheduled-news-" + Workflow.randomUUID())
                        // Родительский scheduler-workflow может завершиться, не отменяя дочерний процесс новости.
                        .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                        .build()
        );

        NewsEvent scheduledNews = new NewsEvent(
                Workflow.randomUUID(),
                "Поздравляем с новой минутой",
                "Temporal scheduler создал эту новость автоматически.",
                NewsStatus.NEW
        );

        log.info("Scheduled workflow generated newsId={} and starts child news workflow", scheduledNews.getId());
        Async.procedure(childWorkflow::process, scheduledNews);
        log.info("Scheduled workflow finished. workflowId={}", Workflow.getInfo().getWorkflowId());
    }
}
