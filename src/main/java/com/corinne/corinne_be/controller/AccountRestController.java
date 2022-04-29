package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRestController {

    private final AccountService accountService;

    @Autowired
    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;

    }

    // 보유 자산
    @GetMapping("/api/account/balance")
    public ResponseEntity<?> getBalance(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.getBalance(userDetails.getUser());
    }
    
    // 모의 투자 페이지 자산
    @GetMapping("/api/account/balance/{coinName}")
    public ResponseEntity<?> getSimpleBalance(@PathVariable String coinName, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.getSimpleBalance(coinName, userDetails.getUser());
    }

}
