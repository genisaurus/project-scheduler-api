package com.russell.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class SchedulerDriver {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerDriver.class, args);
    }
}
