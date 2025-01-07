package com.example.fridayEvening.worker.prepare.work;

import com.example.fridayEvening.service.GodService;
import com.example.fridayEvening.service.MessageSender;
import com.example.fridayEvening.worker.prepare.work.dto.PetInfo;
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
public class HaveBreakfastTask {

    private static final String TASK_TYPE = "haveBreakfastTask";

    private final GodService godService;
    private final MessageSender messageSender;

    @JobWorker(type = TASK_TYPE, autoComplete = false)
    public void haveBreakfastTask(final JobClient client, final ActivatedJob job) {
        long processInstanceKey = job.getProcessInstanceKey();

        log.info(TASK_STARTED, TASK_TYPE, processInstanceKey);

        try {
            godService.haveBreakfast();
            boolean hasPet = godService.hasPet();
            sendZeebeCompleteCommand(client, job.getKey(), new PetInfo().setHasPet(hasPet));
        } catch (Exception e) {
            log.error("Caught error when trying to have breakfast {} [{}]", e.getMessage(), processInstanceKey);
            messageSender.sendMessage(client, job, ApologyCode.COULD_NOT_HAVE_BREAKFAST.getCode());
        }
    }
}
