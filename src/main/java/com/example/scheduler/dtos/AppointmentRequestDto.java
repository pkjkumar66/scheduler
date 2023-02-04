package com.example.scheduler.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDto {
    private String customerEmail;
    private String operatorEmail;
    private String date;
    private AppointmentMetaData metaData;
}
