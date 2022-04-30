package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RankController {

    private final RankService rankService;

    @Autowired
    public RankController(RankService rankService) {
        this.rankService = rankService;
    }

    // 랭킹리스트
    @GetMapping("/api/rank/{page}")
    public ResponseEntity<?> getRank(@PathVariable int page, @AuthenticationPrincipal UserDetailsImpl userDetails){

        return rankService.getRank(page, userDetails.getUser());
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
