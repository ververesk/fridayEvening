package com.example.fridayEvening.worker;

import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StayHomeAndOrderPizzaTask {

    @ZeebeWorker(type = "stayHomeAndOrderPizza", autoComplete = true)
    public void stayHomeAndOrderPizza() {
        log.info("works worker stayHomeAndOrderPizza");
    }
}
