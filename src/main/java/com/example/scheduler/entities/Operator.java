package com.example.scheduler.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(
        name = "operator",
        uniqueConstraints = @UniqueConstraint(name = "operator_email_idx", columnNames = {"email_id"})
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Operator {

    @Id
    @SequenceGenerator(
            name = "operator_sequence",
            sequenceName = "operator_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "operator_sequence"
    )
    @Column(
            name = "id"
    )
    private Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String name;

    @Column(
            name = "email_id",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String email;
}
