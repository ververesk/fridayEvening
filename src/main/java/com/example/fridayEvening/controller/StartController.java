package com.example.fridayEvening.controller;

import com.example.fridayEvening.controller.dto.ProcessData;
import com.example.fridayEvening.controller.dto.ProcessVariables;
import com.example.fridayEvening.service.StartService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/start")
@RequiredArgsConstructor
public class StartController {

    private final StartService startService;

    @PostMapping("/friday/evening")
    public ProcessData startFridayEveningProcess(@RequestBody ProcessVariables request) {
        return startService.startFridayEveningProcess(request);
    }

    @PostMapping("/prepare/work")
    public ProcessData startPrepareToWorkProcess() {
        return startService.startPrepareToWorkProcess();
    }
}
