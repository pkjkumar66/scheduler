package com.example.scheduler.entities;

import com.example.scheduler.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(
        name = "appointment",
        uniqueConstraints = @UniqueConstraint(
                name = "appointment_uidx",
                columnNames = {"operatorId", "customerId", "startTime", "endTime", "date"})
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    @SequenceGenerator(
            name = "appointment_sequence",
            sequenceName = "appointment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "appointment_sequence"
    )
    @Column(
            name = "id"
    )
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
    @Enumerated(EnumType.STRING)
    private Status status;
}
