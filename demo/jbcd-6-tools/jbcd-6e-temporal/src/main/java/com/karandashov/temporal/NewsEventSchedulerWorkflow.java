package com.karandashov.temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

// Отдельный workflow, который запускается по Temporal Schedule.
@WorkflowInterface
public interface NewsEventSchedulerWorkflow {

    @WorkflowMethod
    void publishMinuteGreeting();
}
