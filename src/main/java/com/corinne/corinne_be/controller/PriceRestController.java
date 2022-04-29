package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceRestController {

    private final PriceService priceService;

    @Autowired
    public PriceRestController(PriceService priceService) {
        this.priceService = priceService;
    }

    // 분봉
    @GetMapping("/api/price/minute/{page}")
    public ResponseEntity<?> getMinute(@PathVariable int page){

        int pageNum = page - 1;
        int size = 3;
        String sortBy = "date";
        return priceService.getMinute(pageNum,size,sortBy);
    }


    // 일봉
    @GetMapping("/api/price/date/{page}")
    public ResponseEntity<?> getDate(@PathVariable int page){

        int pageNum = page - 1;
        int size = 3;
        String sortBy = "date";
        return priceService.getdate(pageNum,size,sortBy);
    }

    // 일별 등락률 랭크
    @GetMapping("/api/price/rank")
    public ResponseEntity<?> getDateRank(){

        return priceService.getDateRank();
    }


}
