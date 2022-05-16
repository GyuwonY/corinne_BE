package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.account_dto.AccountResponseDto;
import com.corinne.corinne_be.dto.account_dto.AccountSimpleDto;
import com.corinne.corinne_be.dto.account_dto.BookMarkDto;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;

    }

    // 보유 자산
    @GetMapping("/api/account/balance")
    public ResponseEntity<AccountResponseDto> getBalance(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.getBalance(userDetails.getUser());
    }
    
    // 모의 투자 페이지 자산
    @GetMapping("/api/account/balance/{tiker}")
    public ResponseEntity<AccountSimpleDto> getSimpleBalance(@PathVariable String tiker, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.getSimpleBalance(tiker, userDetails.getUser());
    }

    // 보유 자산 리셋
    @PutMapping("/api/accoint/reset")
    public ResponseEntity<HttpStatus> resetAccount(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.resetAccount(userDetails.getUser());
    }

    // 즐겨찾기 추가
    @GetMapping("/api/account/bookmark/{tiker}")
    public ResponseEntity<?> inputBookmark(@PathVariable String tiker, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.inputBookmark(tiker, userDetails.getUser());
    }

    // 즐겨찾기 삭제
    @DeleteMapping("/api/account/bookmark")
    public ResponseEntity<?> deleteBookmark(@RequestBody BookMarkDto bookMarkDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return accountService.deleteBookmark(bookMarkDto.getTiker(), userDetails.getUser());
    }

}















