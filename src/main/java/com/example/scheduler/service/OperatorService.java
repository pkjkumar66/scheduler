package com.example.scheduler.service;

import com.example.scheduler.dtos.OperatorDto;

public interface OperatorService {
    OperatorDto addOperator(OperatorDto requestDto);

    OperatorDto getOperatorDetails(String emailId);
}
