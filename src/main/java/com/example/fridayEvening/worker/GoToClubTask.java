package com.example.fridayEvening.worker;

import com.example.fridayEvening.worker.dto.Contact;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoToClubTask {

    @ZeebeWorker(type = "goToClub", autoComplete = true)
    public Contact goToClub() {
        log.info("works worker goToClub");
        //рандомно определяем количество выпитых шотов
        int numberOfShots = (int) (Math.random() * 15);
        if (numberOfShots > 8) {
            throw new ZeebeBpmnError("TOO_MUCH_ALCOHOL", "you drank shots= " + numberOfShots);
        } else {
            //уходим с контактом HR о работе
            return new Contact().setTgContact("@hr_contact");
        }
    }
}
