package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.rank_dto.RankInfoDto;
import com.corinne.corinne_be.dto.socket_dto.ChatMessage;
import com.corinne.corinne_be.exception.CustomException;
import com.corinne.corinne_be.exception.ErrorCode;
import com.corinne.corinne_be.model.*;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.utils.AlarmUtil;
import com.corinne.corinne_be.utils.RankUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FollowerService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final AlarmRepository alarmRepository;
    private final RankUtil rankUtil;
    private final QuestRepository questRepository;
    private final AlarmUtil alarmUtil;

    // 팔로우
    @Transactional
    public ResponseEntity<HttpStatus> save(Long userId, User user) {
        //로그인된 유저
        if (userId.equals(user.getUserId())) {
            throw new CustomException(ErrorCode.WRONG_TARGET_FOLLOW);
        }
        //팔로우할 유저
        User follower = userRepository.findById(userId).orElse(null);
        if (follower == null) {
            throw new CustomException(ErrorCode.NON_EXIST_USER);
        }

        Quest quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 7).orElse(null);

        if(quest != null){
            if(!quest.isClear()){
                quest.update(true);
                alarmUtil.sendAlarm(userId.toString());
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
    public ResponseEntity<HttpStatus> unfollow (Long userid, UserDetailsImpl userDetails){
        // 로그인된 유저
        User user = userDetails.getUser();
        // 언팔로우 할 유저
        User follower = userRepository.findById(userid).orElse(null);
        if (follower == null) {
            throw new CustomException(ErrorCode.NON_EXIST_USER);
        }
        followerRepository.deleteByUserAndFollower(user, follower);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<RankInfoDto>> followingList (User loginUser){

        Map<Long, RankInfoDto> rankDtos = rankUtil.getRankMap();

        // 로그인한 유저가 팔로우한 유저 리스트
        List<Follower> followerList = followerRepository.findAllByUser(loginUser);

        // response 값을 담은 리스트
        List<RankInfoDto> followingDtoList = new ArrayList<>();

        for (Follower follower : followerList) {
            followingDtoList.add(rankDtos.get(follower.getFollower().getUserId()));
        }

        // 랭킹 순으로 정렬 해주기
        followingDtoList = followingDtoList.stream().sorted(Comparator.comparing(RankInfoDto::getRank)).collect(Collectors.toList());

        return new ResponseEntity<>(followingDtoList, HttpStatus.OK);
    }
}
