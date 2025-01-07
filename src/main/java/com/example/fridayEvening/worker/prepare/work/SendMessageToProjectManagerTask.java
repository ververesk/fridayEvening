package com.example.fridayEvening.worker.prepare.work;

import com.example.fridayEvening.service.GodService;
import com.example.fridayEvening.service.MessageSender;
import com.example.fridayEvening.worker.prepare.work.dto.ApologyMessageData;
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
public class SendMessageToProjectManagerTask {

    private static final String TASK_TYPE = "sendMessageToProjectManagerTask";


    @JobWorker(type = TASK_TYPE, fetchAllVariables = true, autoComplete = false)
    public void sendMessageToProjectManagerTask(final JobClient client, final ActivatedJob job) {
        long processInstanceKey = job.getProcessInstanceKey();
        log.info(TASK_STARTED, TASK_TYPE, processInstanceKey);
        ApologyMessageData data = job.getVariablesAsType(ApologyMessageData.class);
        log.info("Received apologyCode {}", data.getApologyCode());

        // В реальном проекте тут бы отправляли например сообщение в кафку
        sendZeebeCompleteCommand(client, job.getKey());
    }
}
