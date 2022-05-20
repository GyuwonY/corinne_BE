package com.corinne.corinne_be.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpStatus> tokenExpiredException(TokenExpiredException e){
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
