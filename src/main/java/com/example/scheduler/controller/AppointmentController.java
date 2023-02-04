package com.example.scheduler.controller;

import com.example.scheduler.dtos.AppointmentDto;
import com.example.scheduler.dtos.AppointmentRequestDto;
import com.example.scheduler.dtos.BookAppointmentDto;
import com.example.scheduler.dtos.Interval;
import com.example.scheduler.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/appointment/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    ResponseEntity<AppointmentDto> bookAppointment(@RequestBody @NonNull BookAppointmentDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(appointmentService.bookAppointment(requestDto));
    }

    @PostMapping("/reschedule_or_cancel")
    ResponseEntity<AppointmentDto> rescheduleOrCancelAppointment(@RequestBody @NonNull AppointmentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(appointmentService.rescheduleOrCancelAppointment(requestDto));
    }

    @GetMapping("/booked_slots/{email}")
    ResponseEntity<List<Interval>> getAllBookedSlots(@PathVariable String email, @RequestParam() String date) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(appointmentService.getOperatorBookedSlots(email, date));
    }

    @GetMapping("/open_slots/{email}")
    ResponseEntity<List<Interval>> getAllOpenSlots(@PathVariable String email, @RequestParam() String date) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(appointmentService.getOpenSlots(email, date));
    }
}
