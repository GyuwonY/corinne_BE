package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.transaction_dto.BuyRequestDto;
import com.corinne.corinne_be.dto.transaction_dto.SellRequestDto;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionRestController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 거래내역
    @GetMapping("/api/transaction/{page}")
    public ResponseEntity<?>  getTransactional(@PathVariable int page, @AuthenticationPrincipal UserDetailsImpl userDetails){

        int pageNum = page - 1;
        int size = 3;
        String sortBy = "tradeAt";
        return transactionService.getTransactional(pageNum,size,sortBy,userDetails.getUser());
    }

    // 해당 코인 거래 내역
    @GetMapping("/api/transaction/{coinName}/{page}")
    public ResponseEntity<?>  getSpecifiedTranstnal(@PathVariable String coinName,@PathVariable int page, @AuthenticationPrincipal UserDetailsImpl userDetails){

        int pageNum = page - 1;
        int size = 3;
        String sortBy = "tradeAt";
        return transactionService.getSpecifiedTranstnal(pageNum,size,sortBy,coinName,userDetails.getUser());
    }

    // 매수
    @PostMapping("/api/transaction/buy")
    public ResponseEntity<?>  buy(@RequestBody BuyRequestDto buyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return transactionService.buy(buyRequestDto,userDetails.getUser());
    }


    // 매도
    @PostMapping("/api/transaction/sell")
    public ResponseEntity<?>  sell(@RequestBody SellRequestDto sellRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return transactionService.sell(sellRequestDto,userDetails.getUser());
    }
}


















