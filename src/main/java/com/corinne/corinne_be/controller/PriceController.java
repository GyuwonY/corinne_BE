package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.candle_dto.DatePageDto;
import com.corinne.corinne_be.dto.candle_dto.DateReponseDto;
import com.corinne.corinne_be.dto.candle_dto.MinutePageDto;
import com.corinne.corinne_be.dto.socket_dto.PricePublishingDto;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PriceController {

    private final PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    // 분봉
    @GetMapping("/api/price/minute/{tiker}")
    public ResponseEntity<List<MinutePageDto>> minuteCandleList(@PathVariable String tiker){
        return priceService.minuteCandleList(tiker);
    }


    // 일봉
    @GetMapping("/api/price/date/{tiker}")
    public ResponseEntity<List<DatePageDto>> dateCandleList(@PathVariable String tiker){
        return priceService.dateCandleList(tiker);
    }

    // 일별 등락률 랭크
    @GetMapping("/api/price/rank")
    public ResponseEntity<List<DateReponseDto>> dateRankList(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return priceService.dateRankList(userDetails.getUser());
    }

    @GetMapping("/api/price/tradeprice/{tiker}")
    public ResponseEntity<PricePublishingDto> tradePrice(@PathVariable String tiker){
        return priceService.tradePrice(tiker);
    }
}
