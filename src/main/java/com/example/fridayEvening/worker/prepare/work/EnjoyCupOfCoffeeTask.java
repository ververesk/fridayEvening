package com.example.fridayEvening.worker.prepare.work;

import com.example.fridayEvening.service.GodService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.fridayEvening.util.Constants.TASK_STARTED;
import static com.example.fridayEvening.util.ZeebeCommandBuilder.sendZeebeCompleteCommand;
import static com.example.fridayEvening.util.ZeebeCommandBuilder.sendZeebeThrowErrorCommand;
import static com.example.fridayEvening.util.ZeebeExceptionConstants.INTERNAL_SERVER_ERROR_EXCEPTION_CODE;
import static com.example.fridayEvening.util.ZeebeExceptionConstants.INTERNAL_SERVER_ERROR_EXCEPTION_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnjoyCupOfCoffeeTask {

    private static final String TASK_TYPE = "enjoyCupOfCoffeeTask";
    private final GodService godService;

    @JobWorker(type = TASK_TYPE, autoComplete = false)
    public void enjoyCupOfCoffeeTask(final JobClient client, final ActivatedJob job) {
        long processInstanceKey = job.getProcessInstanceKey();

        log.info(TASK_STARTED, TASK_TYPE, processInstanceKey);

        try {
            godService.enjoyCupOfCoffeeTask();
            sendZeebeCompleteCommand(client, job.getKey());
        } catch (Exception e) {
            log.error("Caught error when trying to enjoy cup of coffee {} [{}]", e.getMessage(), processInstanceKey);
            sendZeebeThrowErrorCommand(client, job.getKey(),
                INTERNAL_SERVER_ERROR_EXCEPTION_CODE, INTERNAL_SERVER_ERROR_EXCEPTION_MESSAGE);
        }
    }
}
