package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.follow_dto.FollowDto;
import com.corinne.corinne_be.dto.rank_dto.RankInfoDto;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.dto.user_dto.UserInfoResponesDto;
import com.corinne.corinne_be.model.Follower;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.FollowerRepository;
import com.corinne.corinne_be.repository.RedisRepository;
import com.corinne.corinne_be.repository.UserRepository;
import com.corinne.corinne_be.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class FollowerService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final CoinRepository coinRepository;
    private final RedisRepository redisRepository;


    @Transactional
    public ResponseEntity<?> save(Long userId, User user) {
        //로그인된 유저
        if (userId.equals(user.getUserId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //팔로우할 유저
        User follower = userRepository.findById(userId).orElse(null);
        if (follower == null) {
            return new ResponseEntity<>("존재하지 않는 유저입니다", HttpStatus.BAD_REQUEST);
        }
        boolean follower1 = followerRepository.existsByUserAndFollower(user, follower);
        if (!follower1) {
            followerRepository.save(new Follower(user, follower));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> unfollow (Long userid, UserDetailsImpl userDetails){
        // 로그인된 유저
        User user = userDetails.getUser();
        // 언팔로우 할 유저
        User follower = userRepository.findById(userid).orElse(null);
        if (follower == null) {
            return new ResponseEntity<>("존재하지 않는 유저입니다", HttpStatus.BAD_REQUEST);
        }
        followerRepository.deleteByUserAndFollower(user, follower);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> getfollowing (User loginUser){

        // 총 유저 리스트
        List<User> Users = userRepository.findAll();

        // 총 유저리스트의 정보리스트
        List<RankInfoDto> rankDtos = new ArrayList<>();

        for(User user : Users){
            // 유저가 보유중인 코인리스트
            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            Long totalBalance = getTotalCoinBalance(coins) + user.getAccountBalance();

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();


            RankInfoDto rankDto = new RankInfoDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate);
            rankDtos.add(rankDto);
        }

        // 보유 자산 순으로 정렬
        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankInfoDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<Long> userIds = new ArrayList<>();

        for(RankInfoDto rankDto : rankDtos){
            userIds.add(rankDto.getUserId());
        }

        // 로그인한 유저를 팔로우한 유저 리스트
        List<Follower> followerList = followerRepository.findAllByUser(loginUser);

        // response 값을 담은 리스트
        List<FollowDto> followingDtoList = new ArrayList<>();

        for (Follower follower : followerList) {

            User followUser = follower.getFollower();
            String nickname = followUser.getNickname();
            int exp = followUser.getExp();
            String imageUrl = followUser.getImageUrl();

            int rankIndex = userIds.indexOf(followUser.getUserId());

            double fluctuationRate = rankDtos.get(rankIndex).getFluctuationRate();
            Long totalBalance = rankDtos.get(rankIndex).getTotalBalance();


            FollowDto followDto = new FollowDto(followUser.getUserId(),nickname,exp,rankIndex+1,fluctuationRate,imageUrl,totalBalance);
            followingDtoList.add(followDto);
        }

        // 팔로우도 랭킹 순을 정렬
        followingDtoList = followingDtoList.stream().sorted(Comparator.comparing(FollowDto::getFluctuationRate).reversed()).collect(Collectors.toList());


        return new ResponseEntity<>(followingDtoList, HttpStatus.OK);
    }
    public Long getTotalCoinBalance(List<Coin> coins){

        Long totalcoinBalance = 0L;

        for(Coin coin : coins){

            // 살 당시 코인 현재가
            BigDecimal buyPrice = BigDecimal.valueOf(coin.getBuyPrice());
            // 현재가
//                BigDecimal currentPrice = BigDecimal.valueOf(redisRepository.getTradePrice(coin.getTiker()));
            BigDecimal currentPrice = BigDecimal.valueOf(100);
            // 래버리지
            BigDecimal leverage = BigDecimal.valueOf(coin.getLeverage());
            // 구매 총금액
            BigDecimal amount = BigDecimal.valueOf(coin.getAmount());
            // 래버리지 적용한 수익률
            BigDecimal fluctuationRate = currentPrice.subtract(buyPrice).multiply(leverage).divide(buyPrice,2,RoundingMode.HALF_UP);
            // 현재 해당 코인의 가치
            Long coinBalance = fluctuationRate.add(BigDecimal.valueOf(1)).multiply(amount).setScale(0,RoundingMode.CEILING).longValue();

            totalcoinBalance += coinBalance;
        }
        return totalcoinBalance;
    }
}
