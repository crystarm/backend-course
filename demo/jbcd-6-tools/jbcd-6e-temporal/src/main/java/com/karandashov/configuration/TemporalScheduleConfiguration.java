package com.karandashov.configuration;

import com.karandashov.service.NewsEventService;
import com.karandashov.temporal.NewsEventSchedulerWorkflow;
import io.temporal.api.enums.v1.ScheduleOverlapPolicy;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.*;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
public class TemporalScheduleConfiguration {

    public static final String MINUTE_GREETING_SCHEDULE_ID = "minute-greeting-schedule";

    /**
     * Отдельный клиент Temporal Schedule API.
     */
    @Bean
    public ScheduleClient scheduleClient(WorkflowServiceStubs serviceStubs) {
        return ScheduleClient.newInstance(serviceStubs);
    }

    /**
     * При старте приложения гарантируем, что минутное расписание существует.
     */
    @Bean
    public ApplicationRunner minuteGreetingScheduleInitializer(ScheduleClient scheduleClient) {
        return args -> {
            Schedule schedule = Schedule.newBuilder()
                    .setAction(
                            ScheduleActionStartWorkflow.newBuilder()
                                    .setWorkflowType(NewsEventSchedulerWorkflow.class)
                                    .setOptions(WorkflowOptions.newBuilder()
                                            .setTaskQueue(NewsEventService.NEWS_TASK_QUEUE)
                                            .setWorkflowId("minute-greeting-workflow")
                                            .build())
                                    .build()
                    )
                    .setSpec(ScheduleSpec.newBuilder()
                            .setIntervals(List.of(new ScheduleIntervalSpec(Duration.ofMinutes(1))))
                            .build())
                    .setPolicy(SchedulePolicy.newBuilder()
                            .setOverlap(ScheduleOverlapPolicy.SCHEDULE_OVERLAP_POLICY_SKIP)
                            .build())
                    .build();

            try {
                scheduleClient.createSchedule(
                        MINUTE_GREETING_SCHEDULE_ID,
                        schedule,
                        ScheduleOptions.newBuilder().build()
                );
                log.info("Temporal schedule {} has been created", MINUTE_GREETING_SCHEDULE_ID);
            } catch (ScheduleAlreadyRunningException ex) {
                log.info("Temporal schedule {} already exists", MINUTE_GREETING_SCHEDULE_ID);
            }
        };
    }
}
