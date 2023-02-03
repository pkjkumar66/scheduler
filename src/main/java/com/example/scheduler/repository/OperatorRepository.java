package com.example.scheduler.repository;

import com.example.scheduler.entities.Operator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface OperatorRepository extends CrudRepository<Operator, Long> {

    Optional<Operator> findByEmail(String email);
}
