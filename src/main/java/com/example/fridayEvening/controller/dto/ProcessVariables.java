package com.example.fridayEvening.controller.dto;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
public class ProcessVariables {

    /**
     * Количество денег в начале вечера пятницы
     */
    @NotNull
    @Min(value = 50, message = "sumOfMoney should not be less than 50")
    @Max(value = 150, message = "sumOfMoney should not be greater than 150")
    private Integer sumOfMoney;
}
