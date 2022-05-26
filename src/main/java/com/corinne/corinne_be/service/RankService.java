package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.rank_dto.*;
import com.corinne.corinne_be.dto.util_dto.SearchTimeDto;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.utils.RankUtil;
import com.corinne.corinne_be.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class RankService {
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final TransactionRepository transactionRepository;
    private final RankUtil rankUtil;
    private final TimeUtil timeUtil;

    @Autowired
    public RankService(UserRepository userRepository, FollowerRepository followerRepository, TransactionRepository transactionRepository, RankUtil rankUtil, TimeUtil timeUtil) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.transactionRepository = transactionRepository;
        this.rankUtil = rankUtil;
        this.timeUtil = timeUtil;
    }


    // 랭킹 리스트
    @Transactional
    public ResponseEntity<List<RankInfoDto>> rankList(User user) {
        List<RankInfoDto> rankInfoDtos = rankUtil.getRankList();

        for(RankInfoDto rankInfoDto : rankInfoDtos){
            rankInfoDto.setFollow(followerRepository.existsByUser_UserIdAndFollower_UserId(user.getUserId(), rankInfoDto.getUserId()));

            SearchTimeDto date = timeUtil.SearchTime("thisWeek");

            rankInfoDto.setResetCount(transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(rankInfoDto.getUserId(),"reset",date.getStartDate(),date.getEndDate()));

        }

        return new ResponseEntity<>(rankInfoDtos, HttpStatus.OK);
    }


    // 상위 랭킹 리스트
    @Transactional
    public ResponseEntity<RankTopDto> rankTop3List() {

        List<User> users = userRepository.findTop3ByLastFluctuationNotOrderByLastFluctuationDesc(0.0);
        List<RankInfoDto> rankDtos = new ArrayList<>();
        for(User user : users){
            rankDtos.add(new RankInfoDto(user));
        }
        return new ResponseEntity<>(new RankTopDto(rankDtos), HttpStatus.OK);
    }

    // 내 랭킹
    @Transactional
    public ResponseEntity<MyRankDto> myRank(User loginUser) {

        return new ResponseEntity<>(rankUtil.getMyRank(loginUser.getUserId()), HttpStatus.OK);
    }

    // 지난주 랭킹 리스트
    @Transactional
    public ResponseEntity<List<RankDto>> lastweekRankList(User loginUser) {

        List<User> Users = userRepository.findAllByLastFluctuationNotOrderByLastFluctuationDesc(0.0);
        List<RankDto> rankDtos = new ArrayList<>();
        for(User user : Users){

            BigDecimal lastFluctuation = BigDecimal.valueOf(user.getLastFluctuation());

            Long totalBalance = lastFluctuation.add(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(10000)).longValue();

            double fluctuationRate = user.getLastFluctuation();

            boolean follow = followerRepository.existsByUser_UserIdAndFollower_UserId(loginUser.getUserId(),user.getUserId());

            // 지난 주
            SearchTimeDto date = timeUtil.SearchTime("lastWeek");
            
            int restCount = transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(user.getUserId(),"reset", date.getStartDate(),date.getEndDate() ).intValue();

            int exp = user.getExp();

            RankDto rankListDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate, follow,restCount,exp);

            rankDtos.add(rankListDto);
        }


        return new ResponseEntity<>(rankDtos, HttpStatus.OK);
    }


}

























