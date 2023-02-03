package com.example.scheduler.service;

import com.example.scheduler.dtos.AppointmentDto;
import com.example.scheduler.dtos.AppointmentRequestDto;
import com.example.scheduler.dtos.Interval;

import java.sql.Date;
import java.util.List;

public interface AppointmentService {

    AppointmentDto bookAppointment(AppointmentRequestDto requestDto);

    AppointmentDto rescheduleOrCancelAppointment(AppointmentRequestDto requestDto);

    List<Interval> getBookedSlots(Long operatorId, Date date);

    List<Interval> getOpenSlots(Long operatorId, Date date);
}
