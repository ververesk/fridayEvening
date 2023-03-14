package com.example.fridayEvening.worker;

import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UrgentlyGoHomeTask {

    @ZeebeWorker(type = "urgentlyGoHome", autoComplete = true)
    public void urgentlyGoHome() {
        log.info("works worker urgentlyGoHome");
    }
}
