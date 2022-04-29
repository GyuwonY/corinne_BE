package com.corinne.corinne_be.controller;


import com.corinne.corinne_be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService){
        this.userService = userService;
    }

}
