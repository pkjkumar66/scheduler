package com.example.scheduler;

import com.example.scheduler.repository.AppointmentDao;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SchedulerApplication {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public AppointmentDao appointmentDao() {
        return new AppointmentDao(new ModelMapper(), null);
    }

    public static void main(String[] args) {

        SpringApplication.run(SchedulerApplication.class, args);
    }

}
