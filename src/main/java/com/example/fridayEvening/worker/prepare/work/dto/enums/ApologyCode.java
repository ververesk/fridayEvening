package com.example.fridayEvening.worker.prepare.work.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApologyCode {

    COULD_NOT_GET_UP(1),
    COULD_NOT_HAVE_BREAKFAST(2),
    COULD_NOT_FEED_PET(3);

    private final int code;
}
