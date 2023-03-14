package com.example.fridayEvening.worker;

import com.example.fridayEvening.controller.dto.ProcessVariables;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DecideHowSpendFridayNightTask {

    @ZeebeWorker(type = "decideHowSpendFridayNight", autoComplete = true)
    public ProcessVariables decideHowSpendFridayNight(ActivatedJob job) {
        log.info("works worker decideHowSpendFridayNight");
        ProcessVariables variables = job.getVariablesAsType(ProcessVariables.class);
        variables.setSumOfMoney(variables.getSumOfMoney() + 10);
        log.info("sumOfMoney after increase is {}", variables.getSumOfMoney());
        return variables;
    }
}
