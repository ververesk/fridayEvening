package com.example.fridayEvening.worker.prepare.work.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PetInfo extends ZeebeVariable {

    private boolean hasPet;
}
