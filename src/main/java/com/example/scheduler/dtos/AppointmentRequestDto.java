package com.example.scheduler.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDto {
    private String customerEmail;
    private String operatorEmail;
    private Interval interval;
    private Date date;
}
