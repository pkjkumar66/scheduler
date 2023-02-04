package com.example.scheduler.repository;

import com.example.scheduler.entities.Appointment;
import com.example.scheduler.enums.Status;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppointmentRepository
        extends CrudRepository<Appointment, Long> {

    @Query("SELECT c FROM Appointment c WHERE c.operatorId = :operatorId and c.startTime = :startTime and c.endTime = :endTime and c.date = :date")
    Optional<Appointment> findByIdAndDate(Long operatorId, int startTime, int endTime, Date date);

    @Query("SELECT c FROM Appointment c WHERE c.operatorId = :operatorId and c.date = :date and c.status IN :statusList")
    Optional<List<Appointment>> findByOperatorId(Long operatorId, Date date, List<Status> statusList);

    @Query("SELECT c FROM Appointment c WHERE c.customerId = :customerId and c.date = :date and c.status IN :statusList")
    Optional<List<Appointment>> findByCustomerId(Long customerId, Date date, List<Status> statusList);

    @Query("SELECT c FROM Appointment c WHERE c.date = :date and c.status IN :statusList")
    Optional<List<Appointment>> find(Date date, List<Status> statusList);
}
