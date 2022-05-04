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

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:/application.yml"
            +",classpath:/application.properties"
            +",classpath:/aws.yml";


    public static void main(String[] args) {
        new SpringApplicationBuilder(CorinneBeApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }


}
