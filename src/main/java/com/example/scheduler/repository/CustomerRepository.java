package com.example.scheduler.repository;

import com.example.scheduler.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Customer findByEmailId(@NonNull String emailId);
}
