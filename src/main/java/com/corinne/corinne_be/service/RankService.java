package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.rank_dto.*;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.utils.RankUtil;
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

    @Autowired
    public RankService(UserRepository userRepository, FollowerRepository followerRepository, TransactionRepository transactionRepository, RankUtil rankUtil) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.transactionRepository = transactionRepository;
        this.rankUtil = rankUtil;
    }


    // 랭킹 리스트
    @Transactional
    public ResponseEntity<List<RankInfoDto>> rankList(User user) {
        List<RankInfoDto> rankInfoDtos = rankUtil.getRankList();

        for(RankInfoDto rankInfoDto : rankInfoDtos){
            rankInfoDto.setFollow(followerRepository.existsByUser_UserIdAndFollower_UserId(user.getUserId(), rankInfoDto.getUserId()));

            Calendar cal = Calendar.getInstance();
            if(cal.get(Calendar.DAY_OF_WEEK)==1){
                cal.add(Calendar.DATE, -1);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String mondayDate = dateFormat.format(cal.getTime());
            mondayDate += " 00:00:00.000";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime startDate = LocalDateTime.parse(mondayDate, formatter);
            LocalDateTime endDate = LocalDateTime.now();

            rankInfoDto.setResetCount(transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(rankInfoDto.getUserId(),"reset",startDate,endDate));

        }

        return new ResponseEntity<>(rankUtil.getRankList(), HttpStatus.OK);
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

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            if(cal.get(Calendar.DAY_OF_WEEK)==1){
                cal.add(Calendar.DATE, -1);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String mondayDate = dateFormat.format(cal.getTime());
            mondayDate += " 00:00:00.000";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime startDate = LocalDateTime.parse(mondayDate, formatter);
            LocalDateTime endDate = LocalDateTime.now();

            int restCount = transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(user.getUserId(),"reset", startDate,endDate ).intValue();

            int exp = user.getExp();

            RankDto rankListDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate, follow,restCount,exp);

            rankDtos.add(rankListDto);
        }


        return new ResponseEntity<>(rankDtos, HttpStatus.OK);
    }


}

























