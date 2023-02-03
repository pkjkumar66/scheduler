package com.example.scheduler.controller;

import com.example.scheduler.dtos.OperatorDto;
import com.example.scheduler.service.OperatorService;
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
@RequestMapping(value = "api/v1/operator")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperatorController {
    private final OperatorService operatorService;

    @PutMapping("/add")
    ResponseEntity<OperatorDto> addOperatorDetails(@RequestBody OperatorDto operator) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(operatorService.addOperator(operator));
    }

    @GetMapping("/{email}")
    ResponseEntity<OperatorDto> getOperatorDetails(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(operatorService.getOperatorDetails(email));
    }
}
