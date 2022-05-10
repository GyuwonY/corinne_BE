package com.corinne.corinne_be.controller;


import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.FollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;




@RequiredArgsConstructor
@RestController
@RequestMapping("/api/follow")
public class FollowerController {

    private  final FollowerService followerService;

    //팔로우
    @PostMapping("/{userId}")
    public ResponseEntity<?> followerUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return followerService.save(userId, userDetails.getUser());
    }
    //언팔
    @DeleteMapping("/{userid}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return followerService.unfollow(userid, userDetails);
    }

    // 팔로잉 조회
    @GetMapping
    public ResponseEntity<?> getFollowing(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return followerService.getfollowing(userDetails.getUser());
    }
}
