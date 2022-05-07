package com.corinne.corinne_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class CorinneBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorinneBeApplication.class, args);
    }


}
