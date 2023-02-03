package com.example.scheduler.service.impl;

import com.example.scheduler.dtos.CustomerDto;
import com.example.scheduler.entities.Customer;
import com.example.scheduler.repository.CustomerRepository;
import com.example.scheduler.service.CustomerService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceImpl implements CustomerService {
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    @Override
    public CustomerDto addCustomer(CustomerDto requestDto) {
        Customer customer = Customer.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .build();

        Optional<Customer> getExisitingCustomer = customerRepository.findByEmail(requestDto.getEmail());
        if (getExisitingCustomer.isPresent()) {
//            throw new CustomerAlreadyExistsException("Customer already exists!");
            return CustomerDto.builder()
                    .id(getExisitingCustomer.get().getId())
                    .name(requestDto.getName())
                    .email(requestDto.getEmail())
                    .error("Customer already exists!")
                    .build();
        }

        Customer saved = customerRepository.save(customer);
        return modelMapper.map(saved, CustomerDto.class);
    }

    @Override
    public CustomerDto getCustomerDetails(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            Customer c = customer.get();
            return CustomerDto.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .email(c.getEmail())
                    .build();
        }

//        throw new NotFoundException("Customer data doesn't exist for this email");
        return CustomerDto.builder()
                .email(email)
                .error("Customer data doesn't exist for this email")
                .build();
    }
}
