package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.follow_dto.FollowDto;
import com.corinne.corinne_be.model.Follower;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.FollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FollowerController {

    private  final FollowerService followerService;


    @PostMapping("/follow/{userId}")
    public void followerUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
    followerService.save(userId, userDetails);
    }

    @DeleteMapping("/follow/{userid}")
    public void unfollowUser(@PathVariable Long userid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followerService.unfollow(userid, userDetails);
    }
    // 팔로워 조회
    @GetMapping("/follow/{userId}/follower")
    public List<FollowDto> getFollower(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return followerService.getfollower(userId, userDetails);
    }

    // 팔로잉 조회
    @GetMapping("/follow/{userId}/following")
    public List<FollowDto> getFollowing(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return followerService.getfollowing(userId, userDetails);
    }
}
