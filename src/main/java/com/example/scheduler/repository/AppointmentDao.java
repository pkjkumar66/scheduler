package com.example.scheduler.repository;

import com.example.scheduler.dtos.AppointmentDto;
import com.example.scheduler.entities.Appointment;
import com.example.scheduler.enums.Status;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppointmentDao {
    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;

    public AppointmentDao(ModelMapper modelMapper, AppointmentRepository appointmentRepository) {
        this.modelMapper = modelMapper;
        this.appointmentRepository = appointmentRepository;
    }

    public AppointmentDto save(AppointmentDto appointmentDto) {
        Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
        appointment.setStatus(Status.BOOKED);
        Appointment saved = appointmentRepository.save(appointment);
        return modelMapper.map(saved, AppointmentDto.class);
    }

    // need to update
    public AppointmentDto update(AppointmentDto appointmentDto, Status status) {
        Optional<Appointment> optionalAppointment =
                appointmentRepository.findByIdAndDate(appointmentDto.getOperatorId(), appointmentDto.getDate());

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStartTime(appointment.getStartTime());
            appointment.setEndTime(appointment.getEndTime());
            appointment.setDate(appointmentDto.getDate());
            appointment.setStatus(status);

            Appointment saved = appointmentRepository.save(appointment);
            return modelMapper.map(saved, AppointmentDto.class);
        }

        return null;
    }

    public List<AppointmentDto> findAllBookedSlotsByOperatorId(Long operatorId, Date date, List<Status> statusList) {
        Optional<List<Appointment>> bookedAppointments = appointmentRepository.findByOperatorId(operatorId, date, statusList);

        return bookedAppointments.map(appointments -> appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }

    public List<AppointmentDto> findAllBookedSlots(Date date, List<Status> statusList) {
        Optional<List<Appointment>> bookedAppointments = appointmentRepository.find(date, statusList);
        return bookedAppointments.map(appointments -> appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }
}
