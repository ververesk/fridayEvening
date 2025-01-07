package com.example.fridayEvening.worker.prepare.work;

import com.example.fridayEvening.service.GodService;
import com.example.fridayEvening.service.MessageSender;
import com.example.fridayEvening.worker.prepare.work.dto.enums.ApologyCode;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.fridayEvening.util.Constants.TASK_STARTED;
import static com.example.fridayEvening.util.ZeebeCommandBuilder.sendZeebeCompleteCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetUpTask {

    private static final String TASK_TYPE = "getUpTask";

    private final GodService godService;
    private final MessageSender messageSender;

    @JobWorker(type = TASK_TYPE, autoComplete = false)
    public void getUpTask(final JobClient client, final ActivatedJob job) {
        long processInstanceKey = job.getProcessInstanceKey();
        log.info(TASK_STARTED, TASK_TYPE, processInstanceKey);

        try {
            godService.getUp();
            sendZeebeCompleteCommand(client, job.getKey());
        } catch (Exception e) {
            log.error("Caught error when trying to get up {} [{}]", e.getMessage(), processInstanceKey);
            messageSender.sendMessage(client, job, ApologyCode.COULD_NOT_GET_UP.getCode());
        }
    }
}
