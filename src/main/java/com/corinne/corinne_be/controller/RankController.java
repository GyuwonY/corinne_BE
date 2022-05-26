package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.rank_dto.*;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class RankController {

    private final RankService rankService;

    @Autowired
    public RankController(RankService rankService) {
        this.rankService = rankService;
    }

    // 랭킹리스트
    @GetMapping("/api/rank")
    public ResponseEntity<List<RankInfoDto>> rankList(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return rankService.rankList(userDetails.getUser());
    }

    // 상위 랭킹 리스트
    @GetMapping("/api/rank/top3")
    public ResponseEntity<RankTopDto> rankTop3List(){

        return rankService.rankTop3List();
    }

    // 내랭킹
    @GetMapping("/api/rank/myrank")
    public ResponseEntity<MyRankDto> myRank(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return rankService.myRank(userDetails.getUser());
    }

    // 지난주 랭킹 리스트
    @GetMapping("/api/rank/lastweek")
    public ResponseEntity<List<RankDto>> lastweekRankList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return rankService.lastweekRankList(userDetails.getUser());
    }

}
