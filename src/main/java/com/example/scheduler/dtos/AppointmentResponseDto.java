package com.example.scheduler.dtos;

import com.example.scheduler.enums.Status;

public class AppointmentResponseDto {
    private String customerEmail;
    private String operatorEmail;
    private Interval interval;
    private String date;
    private Status status;
}
