package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RankRestController {

    private final RankService rankService;

    @Autowired
    public RankRestController(RankService rankService) {
        this.rankService = rankService;
    }

    // 랭킹리스트
    @GetMapping("/api/rank")
    public ResponseEntity<?> getRank(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return rankService.getRank(userDetails.getUser());
    }

    // 상위 랭킹 리스트
    @GetMapping("/api/rank/top3")
    public ResponseEntity<?> getRankTop3(){

        return rankService.getRankTop3();
    }

    // 내랭킹
    @GetMapping("/api/rank/myrank")
    public ResponseEntity<?> getMyRank(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return rankService.getMyRank(userDetails.getUser());
    }

}
