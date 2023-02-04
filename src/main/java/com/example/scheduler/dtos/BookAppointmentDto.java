package com.example.scheduler.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookAppointmentDto {
    private String customerEmail;
    private String operatorEmail;
    private String date;
    private Interval interval;
}
