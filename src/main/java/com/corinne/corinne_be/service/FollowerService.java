package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.follow_dto.FollowDto;
import com.corinne.corinne_be.dto.user_dto.UserInfoResponesDto;
import com.corinne.corinne_be.model.Follower;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.FollowerRepository;
import com.corinne.corinne_be.repository.UserRepository;
import com.corinne.corinne_be.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class FollowerService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    @Transactional
    public void save(Long userId, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        User follower = userRepository.findById(userId).get();

        boolean follower1 =  followerRepository.existsByUserAndFollower(user,follower);
        if (!follower1) {
            followerRepository.save(new Follower(user, follower));
        }
    }

    @Transactional
    public void unfollow(Long userid, UserDetailsImpl userDetails) {
        // 현재 로그인된 유저 정보 가져오기
        User user = userDetails.getUser();
        // 언팔로우 할 유저 정보
        User follower = userRepository.findById(userid).get();

        followerRepository.deleteByUserAndFollower(user, follower);
    }

    public List<FollowDto> getfollower(Long userid, UserDetailsImpl userDetails){
        User user = userRepository.findById(userid).orElseThrow(
                ()-> new IllegalArgumentException("없는 회원입니다"));

        boolean isloginUser = userid.equals(userDetails.getUser().getUserId());

        List<Follower> followerList = followerRepository.findAllByUser(user);
        List<FollowDto> followDtoList = new ArrayList<>();

        for (Follower follower : followerList){
            User followUser = follower.getUser();
            UserInfoResponesDto followerUserDto = new UserInfoResponesDto(followUser);

            boolean followState = followerRepository.existsByUserAndFollower(userDetails.getUser(), followUser);

            FollowDto followDto = new FollowDto(followerUserDto, followState,isloginUser);
            followDtoList.add(followDto);
        }
        return  followDtoList;
    }

    public  List<FollowDto> getfollowing(Long userId, UserDetailsImpl userDetails){
        boolean isLoginUser = userId.equals(userDetails.getUser().getUserId());

        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("없는 회원입니다"));
        List<Follower> followingList = followerRepository.findAllByFollower(user);
        List<FollowDto> followingDtoList = new ArrayList<>();

        for (Follower following :  followingList){
            User followUser = following.getFollower();
            UserInfoResponesDto followerUserDto = new UserInfoResponesDto(followUser);
            boolean followstate = followerRepository.existsByUserAndFollower(userDetails.getUser(),followUser);

            FollowDto followDto = new FollowDto(followerUserDto, followstate, isLoginUser);
            followingDtoList.add(followDto);
        }
        return followingDtoList;
    }
}
