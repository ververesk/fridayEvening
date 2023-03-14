package com.example.fridayEvening.worker;

import com.example.fridayEvening.worker.dto.AllVariables;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WakeUpOnSaturdayMorningTask {

    @ZeebeWorker(type = "wakeUpOnSaturdayMorning", autoComplete = true, forceFetchAllVariables = true)
    public void wakeUpOnSaturdayMorning(ActivatedJob job) {
        log.info("works worker wakeUpOnSaturdayMorning");
        AllVariables allVariables = job.getVariablesAsType(AllVariables.class);
        log.info("messageId {}, tgContact {}, sumOfMoney {}",
            allVariables.getMessageId(), allVariables.getTgContact(), allVariables.getSumOfMoney());
    }
}
