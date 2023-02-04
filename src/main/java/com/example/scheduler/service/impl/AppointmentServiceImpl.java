package com.example.scheduler.service.impl;

import com.example.scheduler.dtos.AppointmentDto;
import com.example.scheduler.dtos.AppointmentRequestDto;
import com.example.scheduler.dtos.Interval;
import com.example.scheduler.entities.Appointment;
import com.example.scheduler.entities.Customer;
import com.example.scheduler.entities.Operator;
import com.example.scheduler.enums.Status;
import com.example.scheduler.exception.InValidDateException;
import com.example.scheduler.exception.InValidIntervalException;
import com.example.scheduler.exception.NotAvailableSlot;
import com.example.scheduler.exception.NotFoundException;
import com.example.scheduler.repository.AppointmentRepository;
import com.example.scheduler.repository.CustomerRepository;
import com.example.scheduler.repository.OperatorRepository;
import com.example.scheduler.service.AppointmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final OperatorRepository operatorRepository;

    @Autowired
    AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                           ModelMapper modelMapper,
                           CustomerRepository customerRepository,
                           OperatorRepository operatorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.modelMapper = modelMapper;
        this.customerRepository = customerRepository;
        this.operatorRepository = operatorRepository;
    }

    @Override
    public AppointmentDto bookAppointment(AppointmentRequestDto requestDto) {
        validateInput(requestDto);

        Customer customer = customerRepository.findByEmail(requestDto.getCustomerEmail()).get();
        Operator operator = operatorRepository.findByEmail(requestDto.getOperatorEmail()).get();

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .customerId(customer.getId())
                .operatorId(operator.getId())
                .interval(Interval.builder()
                        .startTime(requestDto.getInterval().getStartTime())
                        .endTime(requestDto.getInterval().getEndTime())
                        .build())
                .date(requestDto.getDate())
                .build();

        // any booking
        if (requestDto.getOperatorEmail() == null) {
            List<AppointmentDto> allOpenSlots = getAllOpenSlots(getDateFromString(requestDto.getDate()), requestDto.getInterval());
            if (isNotEmpty(allOpenSlots)) {
                appointmentDto.setOperatorId(allOpenSlots.get(0).getOperatorId());
            } else {
                throw new NotAvailableSlot("There is no any slot available for booking, please try after sometime!");
            }
        } else {
            List<Interval> allBookedSlots = getBookedSlots(requestDto.getOperatorEmail(), requestDto.getDate());
            if (isNotEmpty(allBookedSlots) && allBookedSlots.contains(requestDto.getInterval())) {
                throw new NotAvailableSlot("This slot is not available for booking, please choose other slots!");
            }
        }

        return save(appointmentDto);
    }

    @Override
    public AppointmentDto rescheduleOrCancelAppointment(AppointmentRequestDto requestDto) {
        validateInput(requestDto);

        List<Interval> allBookedSlots = getBookedSlots(requestDto.getOperatorEmail(), requestDto.getDate());
        Customer customer = customerRepository.findByEmail(requestDto.getCustomerEmail()).get();
        Operator operator = operatorRepository.findByEmail(requestDto.getOperatorEmail()).get();

        if (isNotEmpty(allBookedSlots)
                && Objects.nonNull(requestDto.getInterval())
                && allBookedSlots.contains(requestDto.getInterval())) {
            throw new NotAvailableSlot("This slot is not available for reschedule, please choose other slots!");
        }

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .customerId(customer.getId())
                .operatorId(operator.getId())
                .interval(Interval.builder()
                        .startTime(requestDto.getInterval().getStartTime())
                        .endTime(requestDto.getInterval().getEndTime())
                        .build())
                .date(requestDto.getDate())
                .build();

        if (Objects.nonNull(requestDto.getInterval())) {
            update(appointmentDto, Status.RESCHEDULED);
        } else {
            update(appointmentDto, Status.CANCELLED);
        }

        return null;
    }

    @Override
    public List<Interval> getBookedSlots(String email, String date) {
        Optional<Operator> operator = operatorRepository.findByEmail(email);
        List<AppointmentDto> bookedAppointments =
                findAllBookedSlotsByOperatorId(operator.get().getId(), Date.valueOf(date), List.of(Status.BOOKED, Status.RESCHEDULED));
        return bookedAppointments.stream()
                .map(appointment -> Interval.builder()
                        .startTime(appointment.getInterval().getStartTime())
                        .endTime(appointment.getInterval().getEndTime())
                        .build())
                .toList();
    }

    @Override
    public List<Interval> getOpenSlots(String email, String date) {
        List<Interval> allIntervals = getAllIntervals();
        List<Interval> bookedSlots = getBookedSlots(email, date);
        allIntervals.removeAll(bookedSlots);
        List<Interval> openSlots = merge(allIntervals);

        if (isNotEmpty(openSlots)) {
            return openSlots;
        }

        return Collections.emptyList();
    }


    private List<AppointmentDto> getAllOpenSlots(Date date, Interval interval) {
        List<AppointmentDto> bookedAppointments =
                findAllBookedSlots(date, List.of(Status.BOOKED, Status.RESCHEDULED));

        // map of (operatorId, bookedSlots)
        Map<Long, List<AppointmentDto>> appointmentDtoMap = new HashMap<>();
        for (AppointmentDto appointmentDto : bookedAppointments) {
            Long operatorId = appointmentDto.getOperatorId();
            if (appointmentDtoMap.containsKey(operatorId)) {
                List<AppointmentDto> dtoList = appointmentDtoMap.get(operatorId);
                dtoList.add(appointmentDto);
                appointmentDtoMap.put(operatorId, dtoList);
            } else {
                appointmentDtoMap.put(operatorId, List.of(appointmentDto));
            }
        }

        // map of (operatorId, openSlots)
        Map<Long, List<AppointmentDto>> openSlotsMap = new HashMap<>();
        appointmentDtoMap.forEach((k, v) -> {
            List<Interval> allIntervals = getAllIntervals();
            List<Interval> bookedSlots = v.stream()
                    .map(appointment -> Interval.builder()
                            .startTime(appointment.getInterval().getStartTime())
                            .endTime(appointment.getInterval().getEndTime())
                            .build())
                    .toList();
            allIntervals.removeAll(bookedSlots);

            List<AppointmentDto> appointmentDtos = new ArrayList<>();
            for (Interval i : allIntervals) {
                AppointmentDto appointmentDto = AppointmentDto.builder()
                        .operatorId(k)
                        .interval(Interval.builder()
                                .startTime(i.getStartTime())
                                .endTime(i.getEndTime())
                                .build())
                        .date(date.toString())
                        .build();

                appointmentDtos.add(appointmentDto);
            }
            openSlotsMap.put(k, appointmentDtos);
        });


        // collect those open slots which are matching with the user provided interval by any operator
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        if (isNotEmpty(openSlotsMap)) {
            openSlotsMap.forEach((k, v) -> {
                        List<AppointmentDto> appointmentDtoList = v.stream()
                                .filter(appointmentDto ->
                                        appointmentDto.getInterval().getStartTime() == interval.getStartTime()
                                                && appointmentDto.getInterval().getEndTime() == interval.getEndTime())
                                .toList();

                        if (isNotEmpty(appointmentDtoList)) {
                            appointmentDtos.addAll(appointmentDtoList);
                        }
                    }
            );
        }

        return appointmentDtos;
    }

    private static List<Interval> getAllIntervals() {
        List<Interval> intervals = new ArrayList<>();
        for (int i = 1; i < 24; i++) {
            Interval interval = Interval.builder()
                    .startTime(i)
                    .endTime(i + 1)
                    .build();
            intervals.add(interval);
        }
        return intervals;
    }

    private List<Interval> merge(List<Interval> intervals) {
        if (intervals.size() <= 1)
            return intervals;

        intervals.sort(Comparator.comparingInt(Interval::getStartTime));

        List<Interval> result = new ArrayList<>();
        int start = intervals.get(0).getStartTime();
        int end = intervals.get(0).getEndTime();

        for (Interval interval : intervals) {
            if (interval.getStartTime() <= end)
                end = Math.max(end, interval.getEndTime());
            else {
                result.add(new Interval(start, end));
                start = interval.getStartTime();
                end = interval.getEndTime();
            }
        }

        result.add(new Interval(start, end));
        return result;
    }

    private void validateInput(AppointmentRequestDto requestDto) {
        if (Objects.nonNull(requestDto)) {

            if (requestDto.getCustomerEmail() == null) {
                throw new IllegalArgumentException("CustomerEmail can't be null");
            }

            Optional<Customer> customer = customerRepository.findByEmail(requestDto.getCustomerEmail());
            if (customer.isEmpty()) {
                throw new NotFoundException("Customer doesn't exists!");
            }

            Interval interval = requestDto.getInterval();
            if (Objects.nonNull(interval) &&
                    interval.getStartTime() >= interval.getEndTime()
                    || interval.getStartTime() < 1
                    || interval.getEndTime() > 24
                    || (interval.getEndTime() - interval.getStartTime() != 1)) {

                throw new InValidIntervalException("Invalid interval");
            }

            if (getDateFromString(requestDto.getDate()).equals(Date.from(Instant.now()))) {
                throw new InValidDateException("Invalid date");
            }
        }
    }

    private AppointmentDto save(AppointmentDto appointmentDto) {
        Appointment appointment = Appointment.builder()
                .customerId(appointmentDto.getCustomerId())
                .operatorId(appointmentDto.getOperatorId())
                .startTime(appointmentDto.getInterval().getStartTime())
                .endTime(appointmentDto.getInterval().getEndTime())
                .date(Date.valueOf(appointmentDto.getDate()))
                .status(Status.BOOKED)
                .build();
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentDto.builder()
                .appointmentId(saved.getId())
                .customerId(saved.getCustomerId())
                .operatorId(saved.getOperatorId())
                .interval(Interval.builder()
                        .startTime(saved.getStartTime())
                        .endTime(saved.getEndTime())
                        .build())
                .date(saved.getDate().toString())
                .status(saved.getStatus())
                .build();
    }

    // need to update
    private AppointmentDto update(AppointmentDto appointmentDto, Status status) {
        Optional<Appointment> optionalAppointment =
                appointmentRepository.findByIdAndDate(appointmentDto.getOperatorId(), Date.valueOf(appointmentDto.getDate()));

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStartTime(appointment.getStartTime());
            appointment.setEndTime(appointment.getEndTime());
            appointment.setDate(Date.valueOf(appointmentDto.getDate()));
            appointment.setStatus(status);
            appointment.setUpdatedAt(Timestamp.from(Instant.now()));
            Appointment saved = appointmentRepository.save(appointment);
            return modelMapper.map(saved, AppointmentDto.class);
        }

        return null;
    }

    private List<AppointmentDto> findAllBookedSlotsByOperatorId(Long operatorId, Date date, List<Status> statusList) {
        Optional<List<Appointment>> bookedAppointments = appointmentRepository.findByOperatorId(operatorId, date, statusList);

        return bookedAppointments.map(appointments -> appointments.stream()
                .map(appointment -> AppointmentDto.builder()
                        .appointmentId(appointment.getId())
                        .customerId(appointment.getCustomerId())
                        .operatorId(appointment.getOperatorId())
                        .date(appointment.getDate().toString())
                        .interval(Interval.builder()
                                .startTime(appointment.getStartTime())
                                .endTime(appointment.getEndTime())
                                .build())
                        .status(appointment.getStatus())
                        .build())
                .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }

    private List<AppointmentDto> findAllBookedSlots(java.util.Date date, List<Status> statusList) {
        Optional<List<Appointment>> bookedAppointments = appointmentRepository.find(date, statusList);
        return bookedAppointments.map(appointments -> appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }

    private Date getDateFromString(String dateString) {
        return Date.valueOf(dateString);
    }
}
