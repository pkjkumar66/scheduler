package com.example.scheduler.service.impl;

import com.example.scheduler.dtos.OperatorDto;
import com.example.scheduler.entities.Operator;
import com.example.scheduler.repository.OperatorRepository;
import com.example.scheduler.service.OperatorService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OperatorServiceImpl implements OperatorService {
    private final ModelMapper modelMapper;
    private final OperatorRepository operatorRepository;

    @Override
    public OperatorDto addOperator(OperatorDto requestDto) {
        Operator operator = Operator.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .build();

        Optional<Operator> getExisitingOperator = operatorRepository.findByEmail(requestDto.getEmail());
        if (getExisitingOperator.isPresent()) {
//            throw new OperatorAlreadyExistsException("Operator already exists!");
            return OperatorDto.builder()
                    .id(getExisitingOperator.get().getId())
                    .name(requestDto.getName())
                    .email(requestDto.getEmail())
                    .error("Operator already exists!")
                    .build();
        }

        Operator saved = operatorRepository.save(operator);
        return modelMapper.map(saved, OperatorDto.class);
    }

    @Override
    public OperatorDto getOperatorDetails(String emailId) {
        Optional<Operator> operator = operatorRepository.findByEmail(emailId);
        if (operator.isPresent()) {
            Operator c = operator.get();
            return OperatorDto.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .email(c.getEmail())
                    .build();
        }

//        throw new NotFoundException("Operator data doesn't exist for this email");
        return OperatorDto.builder()
                .email(emailId)
                .error("Operator data doesn't exist for this emailId")
                .build();
    }
}
