package com.example.fridayEvening.service;

import com.example.fridayEvening.controller.dto.ProcessData;
import com.example.fridayEvening.controller.dto.ProcessVariables;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StartService {

    private static final String FRIDAY_EVENING_PROCESS_NAME = "FRIDAY_EVENING_PROCESS";
    private static final String PREPARE_TO_WORK_PROCESS_NAME = "PREPARE_TO_WORK";

    private final ZeebeClient zeebe;

    public ProcessData startFridayEveningProcess(ProcessVariables request) {
        String uuid = UUID.randomUUID().toString();
        ProcessInstanceEvent instanceEvent = zeebe.newCreateInstanceCommand()
            .bpmnProcessId(FRIDAY_EVENING_PROCESS_NAME)
            .latestVersion()
            .variables(Map.of("sumOfMoney", request.getSumOfMoney(),
                "messageId", uuid))
            .send().join();

        return new ProcessData().setProcessInstanceKey(instanceEvent.getProcessInstanceKey());
    }

    public ProcessData startPrepareToWorkProcess() {
        ProcessInstanceEvent instanceEvent = zeebe.newCreateInstanceCommand()
            .bpmnProcessId(PREPARE_TO_WORK_PROCESS_NAME)
            .latestVersion()
            .send().join();

        return new ProcessData().setProcessInstanceKey(instanceEvent.getProcessInstanceKey());
    }
}

