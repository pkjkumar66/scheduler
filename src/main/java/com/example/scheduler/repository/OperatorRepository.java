package com.example.scheduler.repository;

import com.example.scheduler.entities.Operator;
import org.springframework.lang.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface OperatorRepository extends CrudRepository<Operator, Long> {

    Operator findByEmailId(@NonNull String emailId);
}
