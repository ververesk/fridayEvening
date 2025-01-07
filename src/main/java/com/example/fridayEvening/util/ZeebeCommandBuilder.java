package com.example.fridayEvening.util;

import com.example.fridayEvening.worker.prepare.work.dto.ZeebeVariable;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import lombok.extern.slf4j.Slf4j;

import static com.example.fridayEvening.util.ZeebeExceptionConstants.COMMAND_EXECUTION_EXCEPTION_CODE;
import static com.example.fridayEvening.util.ZeebeExceptionConstants.COMPLETE_COMMAND_EXCEPTION_MESSAGE;
import static com.example.fridayEvening.util.ZeebeExceptionConstants.THROW_ERROR_COMMAND_EXCEPTION_MESSAGE;

@Slf4j
public class ZeebeCommandBuilder {

    private static final String ERROR_EXECUTE_COMMAND = "Caught error when trying to execute command {}";

    public static void sendZeebeCompleteCommand(final JobClient client, long jobKey, ZeebeVariable variable) {
        client.newCompleteCommand(jobKey)
            .variables(variable)
            .send()
            .exceptionally(t -> {
                log.error(ERROR_EXECUTE_COMMAND, t.getMessage());
                throw new ZeebeBpmnError(COMMAND_EXECUTION_EXCEPTION_CODE, COMPLETE_COMMAND_EXCEPTION_MESSAGE, null);
            });
    }

    public static void sendZeebeCompleteCommand(final JobClient client, long jobKey) {
        client.newCompleteCommand(jobKey)
            .send()
            .exceptionally(t -> {
                log.error(ERROR_EXECUTE_COMMAND, t.getMessage());
                throw new ZeebeBpmnError(COMMAND_EXECUTION_EXCEPTION_CODE, COMPLETE_COMMAND_EXCEPTION_MESSAGE, null);
            });
    }

    public static void sendZeebeThrowErrorCommand(final JobClient client, long jobKey, String errorCode, String errorMessage) {
        client.newThrowErrorCommand(jobKey)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .send()
            .exceptionally(t -> {
                log.error(ERROR_EXECUTE_COMMAND, t.getMessage());
                throw new ZeebeBpmnError(COMMAND_EXECUTION_EXCEPTION_CODE, THROW_ERROR_COMMAND_EXCEPTION_MESSAGE, null);
            });
    }
}
