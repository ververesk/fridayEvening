package com.example.fridayEvening.service;

import com.example.fridayEvening.controller.dto.ProcessData;
import com.example.fridayEvening.controller.dto.ProcessVariables;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartService {

    private static final String PROCESS_NAME = "FRIDAY_EVENING_PROCESS";

    private final ZeebeClient zeebe;

    public ProcessData startProcess(ProcessVariables request) {
        String uuid = UUID.randomUUID().toString();
        ProcessInstanceEvent instanceEvent = zeebe.newCreateInstanceCommand()
            .bpmnProcessId(PROCESS_NAME)
            .latestVersion()
            .variables(Map.of("sumOfMoney", request.getSumOfMoney(),
                "messageId", uuid))
            .send().join();

        return new ProcessData().setProcessInstanceKey(instanceEvent.getProcessInstanceKey());
    }
}
