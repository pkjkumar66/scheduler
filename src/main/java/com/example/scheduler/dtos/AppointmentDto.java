package com.example.scheduler.dtos;

import com.example.scheduler.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {
    private Long appointmentId;
    private Long operatorId;
    private Long customerId;
    private Interval interval;
    private String date;

    private Status status;
}
