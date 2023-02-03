package com.example.scheduler.service;

import com.example.scheduler.dtos.CustomerDto;

public interface CustomerService {
    CustomerDto addCustomer(CustomerDto requestDto);

    CustomerDto getCustomerDetails(String email);
}
