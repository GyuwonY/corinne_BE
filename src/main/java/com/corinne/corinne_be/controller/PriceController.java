package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceController {

    private final PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    // 분봉
    @GetMapping("/api/price/minute/{tiker}")
    public ResponseEntity<?> getMinute(@PathVariable String tiker){
        return priceService.getMinute(tiker);
    }


    // 일봉
    @GetMapping("/api/price/date/{tiker}")
    public ResponseEntity<?> getDate(@PathVariable String tiker){
        return priceService.getdate(tiker);
    }

    // 일별 등락률 랭크
    @GetMapping("/api/price/rank")
    public ResponseEntity<?> getDateRank(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return priceService.getDateRank(userDetails.getUser());
    }


}
