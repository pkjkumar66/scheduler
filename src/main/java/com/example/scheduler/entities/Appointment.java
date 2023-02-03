package com.example.scheduler.entities;

import com.example.scheduler.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "appointment")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(
            name = "operatorId",
            nullable = false
    )
    private Long operatorId;

    @Column(
            name = "customerId",
            nullable = false
    )
    private Long customerId;

    @Column(
            name = "startTime",
            nullable = false
    )
    private int startTime;

    @Column(
            name = "endTime",
            nullable = false
    )
    private int endTime;

    @Column(
            name = "date",
            nullable = false
    )
    private Date date;

    @Column(
            name = "status",
            nullable = false
    )
    private Status status;
}
