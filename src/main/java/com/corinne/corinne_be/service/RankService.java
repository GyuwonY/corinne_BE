package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.rank_dto.*;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.utils.RankUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final TransactionRepository transactionRepository;
    private final RankUtil rankUtil;

    @Autowired
    public RankService(CoinRepository coinRepository, UserRepository userRepository, FollowerRepository followerRepository, TransactionRepository transactionRepository, RankUtil rankUtil) {
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.transactionRepository = transactionRepository;
        this.rankUtil = rankUtil;
    }


    // 랭킹 리스트
    public ResponseEntity<?> getRank(int page, User loginUser) {

        // 페이징 사이즈
        int size = 20;

        List<User> Users = userRepository.findAll();

        List<RankDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            Long totalBalance = 0L;

            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            totalBalance = rankUtil.getTotalCoinBalance(coins) +  accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();

            boolean follow = followerRepository.existsByUser_UserIdAndFollower_UserId(loginUser.getUserId(),user.getUserId());

            int restCount = transactionRepository.countByUser_UserIdAndType(user.getUserId(),"reset").intValue();

            int exp = user.getExp();

            RankDto rankListDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate, follow,restCount,exp);

            rankDtos.add(rankListDto);
        }

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<Long> userIds = new ArrayList<>();
        for(RankDto rankDto : rankDtos){
            userIds.add(rankDto.getUserId());
        }

        int myRank = userIds.indexOf(loginUser.getUserId()) + 1;

        // 페이징
        if(page <= 0) {
            return new ResponseEntity<>("올바르지 않는 페이지 요청입니다", HttpStatus.BAD_REQUEST);
        }
        int fromIndex = (page - 1) * size;

        int totalPage = rankDtos.size()/3 + 1;
        if(rankDtos.size()%3 == 0){
            totalPage -= 1;
        }
        if(rankDtos.size() <= fromIndex){
            return new ResponseEntity<>("올바르지 않는 페이지 요청입니다", HttpStatus.BAD_REQUEST);
        }

        MyRankResponseDto myRankResponseDto = new MyRankResponseDto(myRank,rankDtos.subList(fromIndex,Math.min(fromIndex + size, rankDtos.size())), totalPage);

        return  new ResponseEntity<>(myRankResponseDto, HttpStatus.OK);
    }


    // 상위 랭킹 리스트
    public ResponseEntity<?> getRankTop3() {

        List<RankInfoDto> rankDtos = rankUtil.getRankList().subList(0,3);

        return new ResponseEntity<>(new RankTopDto(rankDtos), HttpStatus.OK);
    }

    // 내 랭킹
    public ResponseEntity<MyRankDto> getMyRank(User loginUser) {

        return  new ResponseEntity<>(rankUtil.getMyRank(loginUser.getUserId()), HttpStatus.OK);
    }

}

























