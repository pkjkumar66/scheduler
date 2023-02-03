package com.example.scheduler.controller;

import com.example.scheduler.dtos.CustomerDto;
import com.example.scheduler.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/customer")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerController {

    private final CustomerService customerService;

    @PutMapping("/add")
    ResponseEntity<CustomerDto> addCustomerDetails(@RequestBody CustomerDto customer) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerService.addCustomer(customer));
    }

    @GetMapping("/{email}")
    ResponseEntity<CustomerDto> getCustomerDetails(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerService.getCustomerDetails(email));
    }
}
