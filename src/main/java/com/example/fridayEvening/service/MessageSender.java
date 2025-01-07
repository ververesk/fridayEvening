package com.example.fridayEvening.service;

import com.example.fridayEvening.worker.prepare.work.dto.ApologyMessageData;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.fridayEvening.util.ZeebeCommandBuilder.sendZeebeThrowErrorCommand;
import static com.example.fridayEvening.util.ZeebeExceptionConstants.CRITICAL_ERROR_EXCEPTION_CODE;
import static com.example.fridayEvening.util.ZeebeExceptionConstants.CRITICAL_ERROR_EXCEPTION_MESSAGE;

@Component
@RequiredArgsConstructor
public class MessageSender {

    private final ZeebeClient zeebe;

    public void sendMessage(JobClient client, ActivatedJob job, Integer errorCode) {
        final ApologyMessageData apologyMessageData = new ApologyMessageData()
            .setApologyCode(errorCode);

        saveVariables(job.getElementInstanceKey(), apologyMessageData);

        sendZeebeThrowErrorCommand(client, job.getKey(), CRITICAL_ERROR_EXCEPTION_CODE, CRITICAL_ERROR_EXCEPTION_MESSAGE);
    }

    private void saveVariables(long elementInstanceKey, ApologyMessageData apologyMessageData) {
        zeebe.newSetVariablesCommand(elementInstanceKey)
            .variables(apologyMessageData)
            .send()
            .join();
    }
}
