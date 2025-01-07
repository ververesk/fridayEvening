package com.example.fridayEvening.worker.choose.evening;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWithFriendsPeriodicallyTask {
    private final ZeebeClient zeebe;

    @ZeebeWorker(type = "chatWithFriendsPeriodically", autoComplete = true)
    public void chatWithFriendsPeriodically(@ZeebeVariable String messageId) {
        log.info("works worker chatWithFriendsPeriodically");

        PublishMessageResponse message = zeebe.newPublishMessageCommand()
            .messageName("MEET_MESSAGE")
            .correlationKey(messageId)
            .send()
            .join();
        log.info("There were sent message with messageKey {} and correlationKey {}", message.getMessageKey(), messageId);
    }
}
