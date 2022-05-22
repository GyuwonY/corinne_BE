package com.corinne.corinne_be.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class Exception {

    private String errorMessage;
    private HttpStatus httpStatus;
}