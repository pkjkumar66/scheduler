package com.example.scheduler.service;

import com.example.scheduler.dtos.AppointmentDto;
import com.example.scheduler.dtos.AppointmentRequestDto;
import com.example.scheduler.dtos.BookAppointmentDto;
import com.example.scheduler.dtos.Interval;

import java.util.List;

public interface AppointmentService {

    AppointmentDto bookAppointment(BookAppointmentDto requestDto);

    AppointmentDto rescheduleOrCancelAppointment(AppointmentRequestDto requestDto);

    List<Interval> getOperatorBookedSlots(String email, String date);

    List<Interval> getCustomerBookedSlots(String email, String date);

    List<Interval> getOpenSlots(String email, String date);
}
