package com._A0508.__508.controller;

import com._A0508.__508.service.AverageCalculatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AverageCalculatorController {

    private final AverageCalculatorService averageCalculatorService;

    public AverageCalculatorController(AverageCalculatorService averageCalculatorService) {
        this.averageCalculatorService = averageCalculatorService;
    }

    @GetMapping("/numbers/{numberId}")
    public String calculateAverage(@PathVariable String numberId) {
        return averageCalculatorService.calculateAverage(numberId);
    }
}
