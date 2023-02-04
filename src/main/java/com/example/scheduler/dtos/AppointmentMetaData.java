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
public class AppointmentMetaData {
    private Interval from;
    private Interval to;
    private Status status;
}
