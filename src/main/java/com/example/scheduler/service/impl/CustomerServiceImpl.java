package com.example.scheduler.service.impl;

import com.example.scheduler.dtos.CustomerDto;
import com.example.scheduler.entities.Customer;
import com.example.scheduler.repository.CustomerRepository;
import com.example.scheduler.service.CustomerService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceImpl implements CustomerService {
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    @Override
    public CustomerDto addCustomer(CustomerDto requestDto) {
        Customer customer = Customer.builder()
                .createdAt(Timestamp.from(Instant.now()))
                .updatedAt(Timestamp.from(Instant.now()))
                .name(requestDto.getName())
                .emailId(requestDto.getEmailId())
                .build();
        Customer saved = customerRepository.save(customer);
        return modelMapper.map(saved, CustomerDto.class);
    }

    @Override
    public CustomerDto getCustomerDetails(String emailId) {
        Customer byEmailId = customerRepository.findByEmailId(emailId);
        if (Objects.isNull(byEmailId)) {
            return CustomerDto.builder()
                    .emailId(emailId)
                    .error("Customer data doesn't exist for this emailID")
                    .build();

//            throw new NotFoundException("Customer data doesn't exist for this emailID: " + emailId);
        }
        return modelMapper.map(byEmailId, CustomerDto.class);
    }
}
