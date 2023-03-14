package com.example.fridayEvening.worker;

import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AcceptInvitationTask {
    @ZeebeWorker(type = "acceptInvitation", autoComplete = true)
    public void acceptInvitation(@ZeebeVariable String messageId) {
        log.info("works worker acceptInvitation, messageId is {}", messageId);
    }
}
