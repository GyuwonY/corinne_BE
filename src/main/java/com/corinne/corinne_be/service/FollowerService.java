package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.follow_dto.FollowDto;
import com.corinne.corinne_be.dto.rank_dto.RankInfoDto;
import com.corinne.corinne_be.model.*;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.utils.RankUtil;
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
    private final AlarmRepository alarmRepository;
    private final RankUtil rankUtil;
    private final QuestRepository questRepository;

    // 팔로우
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

        Quest quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 7).orElse(null);

        if(quest != null){
            if(!quest.isClear()){
                quest.update(true);
            }
        }

        boolean follower1 = followerRepository.existsByUserAndFollower(user, follower);
        if (!follower1) {
            followerRepository.save(new Follower(user, follower));
        }

        // 팔로우 알림 등록
        Alarm alarm = new Alarm(follower, Alarm.AlarmType.FOLLWER, user.getNickname());
        alarmRepository.save(alarm);
        follower.alarmUpdate(true);

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

    public ResponseEntity<List<FollowDto>> getfollowing (User loginUser){

        // 총 유저 리스트
        List<User> Users = userRepository.findAll();

        // 총 유저리스트의 정보리스트
        List<RankInfoDto> rankDtos = new ArrayList<>();

        for(User user : Users){
            // 유저가 보유중인 코인리스트
            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            Long totalBalance = rankUtil.getTotalCoinBalance(coins) + user.getAccountBalance();

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
        followingDtoList = followingDtoList.stream().sorted(Comparator.comparing(FollowDto::getRank).reversed()).collect(Collectors.toList());


        return new ResponseEntity<>(followingDtoList, HttpStatus.OK);
    }
}
