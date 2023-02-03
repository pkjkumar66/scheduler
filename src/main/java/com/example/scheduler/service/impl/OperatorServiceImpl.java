package com.example.scheduler.service.impl;

import com.example.scheduler.dtos.OperatorDto;
import com.example.scheduler.entities.Operator;
import com.example.scheduler.repository.OperatorRepository;
import com.example.scheduler.service.OperatorService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OperatorServiceImpl implements OperatorService {
    private final ModelMapper modelMapper;
    private final OperatorRepository operatorRepository;

    @Override
    public OperatorDto addOperator(OperatorDto requestDto) {
        Operator operator = Operator.builder()
                .createdAt(Timestamp.from(Instant.now()))
                .updatedAt(Timestamp.from(Instant.now()))
                .name(requestDto.getName())
                .emailId(requestDto.getEmailId())
                .build();
        Operator saved = operatorRepository.save(operator);
        return modelMapper.map(saved, OperatorDto.class);
    }

    @Override
    public OperatorDto getOperatorDetails(String emailId) {
        Operator byEmailId = operatorRepository.findByEmailId(emailId);
        if (Objects.isNull(byEmailId)) {
            return OperatorDto.builder()
                    .emailId(emailId)
                    .error("Operator data doesn't exist for this emailID")
                    .build();
        }
        return modelMapper.map(byEmailId, OperatorDto.class);
    }
}
