package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.transaction_dto.*;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 거래내역
    @GetMapping("/api/transaction/{page}")
    public ResponseEntity<Page<TransactionResponseDto>> transactionalList(@PathVariable int page, @AuthenticationPrincipal UserDetailsImpl userDetails){

        int pageNum = page - 1;
        int size = 5;
        String sortBy = "tradeAt";
        return transactionService.transactionalList(pageNum,size,sortBy,userDetails.getUser());
    }

    // 해당 코인 거래 내역
    @GetMapping("/api/transaction/{coinName}/{page}")
    public ResponseEntity<Page<TransactionResponseDto>> specifiedTransactional(@PathVariable String coinName, @PathVariable int page, @AuthenticationPrincipal UserDetailsImpl userDetails){

        int pageNum = page - 1;
        int size = 5;
        String sortBy = "tradeAt";
        return transactionService.specifiedTransactional(pageNum,size,sortBy,coinName,userDetails.getUser());
    }

    // 매수
    @PostMapping("/api/transaction/buy")
    public ResponseEntity<BuyResponseDto> buy(@RequestBody BuyRequestDto buyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return transactionService.buy(buyRequestDto,userDetails.getUser());
    }


    // 매도
    @PostMapping("/api/transaction/sell")
    public ResponseEntity<SellResponseDto> sell(@RequestBody SellRequestDto sellRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return transactionService.sell(sellRequestDto,userDetails.getUser());
    }

    // 상대방 최근 거래 내역 보기
    @GetMapping("/api/user/transaction/{userId}")
    public ResponseEntity<UserTranResponseDto> userTranstnal(@PathVariable Long userId){
        return transactionService.userTranstnal(userId);
    }

    // 코린이 회원 중 특정 코인 매수 카운트
    @GetMapping("/api/transaction/buycount/{tiker}")
    public ResponseEntity<BuyCountDto> buyCount(@PathVariable String tiker){
        return transactionService.buyCount(tiker);
    }
}


















