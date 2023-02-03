package com.example.scheduler.service.impl;

import com.example.scheduler.dtos.AppointmentDto;
import com.example.scheduler.dtos.AppointmentRequestDto;
import com.example.scheduler.dtos.Interval;
import com.example.scheduler.enums.Status;
import com.example.scheduler.exception.InValidDateException;
import com.example.scheduler.exception.InValidIntervalException;
import com.example.scheduler.exception.NotAvailableSlot;
import com.example.scheduler.repository.AppointmentDao;
import com.example.scheduler.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentDao appointmentDao;

    @Autowired
    AppointmentServiceImpl(AppointmentDao appointmentDao) {
        this.appointmentDao = appointmentDao;
    }

    @Override
    public AppointmentDto bookAppointment(AppointmentRequestDto requestDto) {
        validateInput(requestDto);

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .customerId(requestDto.getCustomerId())
                .operatorId(requestDto.getOperatorId())
                .startTime(requestDto.getInterval().getStartTime())
                .endTime(requestDto.getInterval().getEndTime())
                .date(requestDto.getDate())
                .build();

        // any booking
        if (requestDto.getOperatorId() == null) {
            List<AppointmentDto> allOpenSlots = getAllOpenSlots(requestDto.getDate(), requestDto.getInterval());
            if (isNotEmpty(allOpenSlots)) {
                appointmentDto.setOperatorId(allOpenSlots.get(0).getOperatorId());
            } else {
                throw new NotAvailableSlot("There is no any slot available for booking, please try after sometime!");
            }
        } else {
            List<Interval> allBookedSlots = getBookedSlots(requestDto.getOperatorId(), requestDto.getDate());
            if (isNotEmpty(allBookedSlots) && allBookedSlots.contains(requestDto.getInterval())) {
                throw new NotAvailableSlot("This slot is not available for booking, please choose other slots!");
            }
        }

        return appointmentDao.save(appointmentDto);
    }

    @Override
    public AppointmentDto rescheduleOrCancelAppointment(AppointmentRequestDto requestDto) {
        validateInput(requestDto);
        List<Interval> allBookedSlots = getBookedSlots(requestDto.getOperatorId(), requestDto.getDate());

        if (isNotEmpty(allBookedSlots)
                && Objects.nonNull(requestDto.getInterval())
                && allBookedSlots.contains(requestDto.getInterval())) {
            throw new NotAvailableSlot("This slot is not available for reschedule, please choose other slots!");
        }

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .customerId(requestDto.getCustomerId())
                .operatorId(requestDto.getOperatorId())
                .startTime(requestDto.getInterval().getStartTime())
                .endTime(requestDto.getInterval().getEndTime())
                .date(requestDto.getDate())
                .build();

        if (Objects.nonNull(requestDto.getInterval())) {
            appointmentDao.update(appointmentDto, Status.RESCHEDULED);
        } else {
            appointmentDao.update(appointmentDto, Status.CANCELLED);
        }

        return null;
    }

    @Override
    public List<Interval> getBookedSlots(Long operatorId, Date date) {
        List<AppointmentDto> bookedAppointments =
                appointmentDao.findAllBookedSlotsByOperatorId(operatorId, date, List.of(Status.BOOKED, Status.RESCHEDULED));
        return bookedAppointments.stream()
                .map(appointment -> Interval.builder()
                        .startTime(appointment.getStartTime())
                        .endTime(appointment.getEndTime())
                        .build())
                .toList();
    }

    @Override
    public List<Interval> getOpenSlots(Long operatorId, Date date) {
        List<Interval> allIntervals = getAllIntervals();
        List<Interval> bookedSlots = getBookedSlots(operatorId, date);
        allIntervals.removeAll(bookedSlots);
        List<Interval> openSlots = merge(allIntervals);

        if (isNotEmpty(openSlots)) {
            return openSlots;
        }

        return Collections.emptyList();
    }


    private List<AppointmentDto> getAllOpenSlots(Date date, Interval interval) {
        List<AppointmentDto> bookedAppointments =
                appointmentDao.findAllBookedSlots(date, List.of(Status.BOOKED, Status.RESCHEDULED));

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
                            .startTime(appointment.getStartTime())
                            .endTime(appointment.getEndTime())
                            .build())
                    .toList();
            allIntervals.removeAll(bookedSlots);

            List<AppointmentDto> appointmentDtos = new ArrayList<>();
            for (Interval i : allIntervals) {
                AppointmentDto appointmentDto = AppointmentDto.builder()
                        .operatorId(k)
                        .startTime(i.getStartTime())
                        .endTime(i.getEndTime())
                        .date(date)
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
                                        appointmentDto.getStartTime() == interval.getStartTime()
                                                && appointmentDto.getEndTime() == interval.getEndTime())
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

            if (requestDto.getCustomerId() == null) {
                throw new IllegalArgumentException("CustomerId can't be null");
            }

            Interval interval = requestDto.getInterval();
            if (Objects.nonNull(interval) &&
                    interval.getStartTime() < 1
                    || interval.getEndTime() > 24
                    || (interval.getEndTime() - interval.getStartTime() != 1)) {

                throw new InValidIntervalException("Invalid interval");
            }

            if (requestDto.getDate().equals(Date.from(Instant.now()))) {
                throw new InValidDateException("Invalid date");
            }
        }
    }
}
