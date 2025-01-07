package com.example.fridayEvening.processcamunda;

import com.example.fridayEvening.service.GodService;
import com.example.fridayEvening.worker.prepare.work.dto.enums.ApologyCode;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceHasPassedElement;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ZeebeSpringTest
public class PrepareToWorkTest {

    private static final String PROCESS_NAME = "PREPARE_TO_WORK";
    private static final String GET_UP_TASK = "Activity_1wms7ji";
    private static final String HAVE_BREAKFAST_TASK = "Activity_01h779y";
    private static final String FEED_PET_TASK = "Activity_1repfv7";
    private static final String ENJOY_CUP_OF_COFFEE_TASK = "Activity_08x2kch";
    private static final String ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE = "Event_077d3lj";
    private static final String ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK = "Activity_13fqgx5";
    private static final String SEND_MESSAGE_TO_PM_TASK = "Event_11mknrr";

    private static final String VARIABLE_HAS_PET = "hasPet";
    private static final String VARIABLE_APOLOGY_CODE = "apologyCode";

    @Autowired
    private ZeebeClient client;

    @MockBean
    private GodService godService;

    @BeforeEach
    void prepareMocks() {
        when(godService.hasPet()).thenReturn(true);
        doNothing().when(godService).getUp();
        doNothing().when(godService).haveBreakfast();
        doNothing().when(godService).feedPet();
        doNothing().when(godService).enjoyCupOfCoffeeTask();
    }

    @Test
    void givenHappyPath_whenExecuteProcess_thenProcessInstanceHasNoIncidents() {
        final ProcessInstanceEvent instanceEvent = runProcessInstance();
        final long processInstanceKey = instanceEvent.getProcessInstanceKey();

        waitForPassingElement(processInstanceKey, GET_UP_TASK);
        waitForPassingElement(processInstanceKey, HAVE_BREAKFAST_TASK);
        waitForPassingElement(processInstanceKey, FEED_PET_TASK);
        waitForPassingElement(processInstanceKey, ENJOY_CUP_OF_COFFEE_TASK);

        BpmnAssert.assertThat(instanceEvent)
            .hasVariableWithValue(VARIABLE_HAS_PET, true)
            .hasPassedElementsInOrder(
                GET_UP_TASK,
                HAVE_BREAKFAST_TASK,
                FEED_PET_TASK,
                ENJOY_CUP_OF_COFFEE_TASK
            )
            .hasNotPassedElement(ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE)
            .hasNotPassedElement(ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK)
            .hasNotPassedElement(SEND_MESSAGE_TO_PM_TASK)
            .isStarted()
            .hasNoIncidents()
            .isCompleted();

        assertAll(
            () -> verify(godService, times(1)).getUp(),
            () -> verify(godService, times(1)).haveBreakfast(),
            () -> verify(godService, times(1)).feedPet(),
            () -> verify(godService, times(1)).enjoyCupOfCoffeeTask()
        );
    }

    @Test
    void givenHasNotPet_whenExecuteProcess_thenDoNotFeedPet() {
        final ProcessInstanceEvent instanceEvent = runProcessInstance();
        final long processInstanceKey = instanceEvent.getProcessInstanceKey();

        when(godService.hasPet()).thenReturn(false);

        waitForPassingElement(processInstanceKey, GET_UP_TASK);
        waitForPassingElement(processInstanceKey, HAVE_BREAKFAST_TASK);
        waitForPassingElement(processInstanceKey, ENJOY_CUP_OF_COFFEE_TASK);

        BpmnAssert.assertThat(instanceEvent)
            .hasVariableWithValue(VARIABLE_HAS_PET, false)
            .hasPassedElementsInOrder(
                GET_UP_TASK,
                HAVE_BREAKFAST_TASK,
                ENJOY_CUP_OF_COFFEE_TASK
            )
            .hasNotPassedElement(FEED_PET_TASK)
            .hasNotPassedElement(ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE)
            .hasNotPassedElement(ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK)
            .hasNotPassedElement(SEND_MESSAGE_TO_PM_TASK)
            .isStarted()
            .hasNoIncidents()
            .isCompleted();

        assertAll(
            () -> verify(godService, times(1)).getUp(),
            () -> verify(godService, times(1)).haveBreakfast(),
            () -> verify(godService, never()).feedPet(),
            () -> verify(godService, times(1)).enjoyCupOfCoffeeTask()
        );
    }

    @Test
    void givenThrowException_whenGetUp_thenSendMessageToPM() {
        final ProcessInstanceEvent instanceEvent = runProcessInstance();
        final long processInstanceKey = instanceEvent.getProcessInstanceKey();

        doThrow(RuntimeException.class).when(godService).getUp();

        waitForPassingElement(processInstanceKey, ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK);
        waitForPassingElement(processInstanceKey, SEND_MESSAGE_TO_PM_TASK);

        BpmnAssert.assertThat(instanceEvent)
            .hasVariableWithValue(VARIABLE_APOLOGY_CODE, ApologyCode.COULD_NOT_GET_UP.getCode())
            .hasPassedElement(ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK)
            .hasPassedElement(SEND_MESSAGE_TO_PM_TASK)
            .hasNotPassedElement(GET_UP_TASK)
            .hasNotPassedElement(HAVE_BREAKFAST_TASK)
            .hasNotPassedElement(FEED_PET_TASK)
            .hasNotPassedElement(ENJOY_CUP_OF_COFFEE_TASK)
            .hasNotPassedElement(ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE)
            .isStarted()
            .hasNoIncidents()
            .isCompleted();

        assertAll(
            () -> verify(godService, times(1)).getUp(),
            () -> verify(godService, never()).haveBreakfast(),
            () -> verify(godService, never()).feedPet(),
            () -> verify(godService, never()).enjoyCupOfCoffeeTask()
        );
    }

    @Test
    void givenThrowException_whenHaveBreakfast_thenSendMessageToPM() {
        final ProcessInstanceEvent instanceEvent = runProcessInstance();
        final long processInstanceKey = instanceEvent.getProcessInstanceKey();

        doThrow(RuntimeException.class).when(godService).haveBreakfast();

        waitForPassingElement(processInstanceKey, GET_UP_TASK);
        waitForPassingElement(processInstanceKey, ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK);
        waitForPassingElement(processInstanceKey, SEND_MESSAGE_TO_PM_TASK);

        BpmnAssert.assertThat(instanceEvent)
            .hasVariableWithValue(VARIABLE_APOLOGY_CODE, ApologyCode.COULD_NOT_HAVE_BREAKFAST.getCode())
            .hasPassedElement(GET_UP_TASK)
            .hasPassedElement(ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK)
            .hasPassedElement(SEND_MESSAGE_TO_PM_TASK)
            .hasNotPassedElement(HAVE_BREAKFAST_TASK)
            .hasNotPassedElement(FEED_PET_TASK)
            .hasNotPassedElement(ENJOY_CUP_OF_COFFEE_TASK)
            .hasNotPassedElement(ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE)
            .isStarted()
            .hasNoIncidents()
            .isCompleted();

        assertAll(
            () -> verify(godService, times(1)).getUp(),
            () -> verify(godService, times(1)).haveBreakfast(),
            () -> verify(godService, never()).feedPet(),
            () -> verify(godService, never()).enjoyCupOfCoffeeTask()
        );
    }

    @Test
    void givenThrowException_whenFeedPet_thenSendMessageToPM() {
        final ProcessInstanceEvent instanceEvent = runProcessInstance();
        final long processInstanceKey = instanceEvent.getProcessInstanceKey();

        doThrow(RuntimeException.class).when(godService).feedPet();

        waitForPassingElement(processInstanceKey, GET_UP_TASK);
        waitForPassingElement(processInstanceKey, HAVE_BREAKFAST_TASK);
        waitForPassingElement(processInstanceKey, ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK);
        waitForPassingElement(processInstanceKey, SEND_MESSAGE_TO_PM_TASK);

        BpmnAssert.assertThat(instanceEvent)
            .hasVariableWithValue(VARIABLE_HAS_PET, true)
            .hasVariableWithValue(VARIABLE_APOLOGY_CODE, ApologyCode.COULD_NOT_FEED_PET.getCode())
            .hasPassedElement(GET_UP_TASK)
            .hasPassedElement(HAVE_BREAKFAST_TASK)
            .hasPassedElement(ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK)
            .hasPassedElement(SEND_MESSAGE_TO_PM_TASK)
            .hasNotPassedElement(ENJOY_CUP_OF_COFFEE_TASK)
            .hasNotPassedElement(ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE)
            .isStarted()
            .hasNoIncidents()
            .isCompleted();

        assertAll(
            () -> verify(godService, times(1)).getUp(),
            () -> verify(godService, times(1)).haveBreakfast(),
            () -> verify(godService, times(1)).feedPet(),
            () -> verify(godService, never()).enjoyCupOfCoffeeTask()
        );
    }

    @Test
    void givenThrowException_whenEnjoyCupOfCoffeeTask_thenProcessGoesOn() {
        final ProcessInstanceEvent instanceEvent = runProcessInstance();
        final long processInstanceKey = instanceEvent.getProcessInstanceKey();

        doThrow(RuntimeException.class).when(godService).enjoyCupOfCoffeeTask();

        waitForPassingElement(processInstanceKey, GET_UP_TASK);
        waitForPassingElement(processInstanceKey, HAVE_BREAKFAST_TASK);
        waitForPassingElement(processInstanceKey, FEED_PET_TASK);
        waitForPassingElement(processInstanceKey, ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE);

        BpmnAssert.assertThat(instanceEvent)
            .hasVariableWithValue(VARIABLE_HAS_PET, true)
            .hasPassedElement(GET_UP_TASK)
            .hasPassedElement(HAVE_BREAKFAST_TASK)
            .hasPassedElement(FEED_PET_TASK)
            .hasNotPassedElement(ENJOY_CUP_OF_COFFEE_TASK)
            .hasPassedElement(ZEEBE_ERROR_ENJOY_CUP_OF_COFFEE)
            .hasNotPassedElement(ZEEBE_ERROR_SEND_MESSAGE_TO_PM_TASK)
            .hasNotPassedElement(SEND_MESSAGE_TO_PM_TASK)
            .isStarted()
            .hasNoIncidents()
            .isCompleted();

        assertAll(
            () -> verify(godService, times(1)).getUp(),
            () -> verify(godService, times(1)).haveBreakfast(),
            () -> verify(godService, times(1)).feedPet(),
            () -> verify(godService, times(1)).enjoyCupOfCoffeeTask()
        );
    }

    private ProcessInstanceEvent runProcessInstance() {
        return client.newCreateInstanceCommand()
            .bpmnProcessId(PROCESS_NAME)
            .latestVersion()
            .send()
            .join();
    }

    private void waitForPassingElement(long processInstanceKey, String elementId) {
        // ждём выполнения воркера 60 сек
        waitForProcessInstanceHasPassedElement(processInstanceKey, elementId, Duration.ofSeconds(60));
    }
}
