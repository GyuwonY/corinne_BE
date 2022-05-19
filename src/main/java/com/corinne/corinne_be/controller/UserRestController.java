package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.user_dto.*;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.KakaoService;
import com.corinne.corinne_be.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class UserRestController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @Autowired
    public UserRestController(UserService userService, KakaoService kakaoService) {
        this.kakaoService = kakaoService;
        this.userService = userService;
    }

    //카카오로그인
    @PostMapping("/user/kakao/callback")
    public ResponseEntity<?> kakao(@RequestBody KakaoDto kakaoDto) throws JsonProcessingException {
        return kakaoService.kakao(kakaoDto.getAuthCode());
    }

    //회원정보 수정
    @PatchMapping("/user/signup")
    public ResponseEntity<?> InfoUpdate(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestdto userRequestdto){
        return userService.InfoUpdate(userDetails.getUser(),userRequestdto);
    }

    //회원정보조희
    @GetMapping("/api/user/info")
    public ResponseEntity<?> Userinfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.UserInfo(userDetails.getUser().getUserId());
    }

    @GetMapping("/api/user/info/{userId}")
    public ResponseEntity<?> Userinfo(@PathVariable Long userId) {
        return userService.UserInfo(userId);
    }

    //프로필이미지 수정
    @PatchMapping("/api/user/image")
    public ProfileResponseDto registImage(@RequestParam("image") MultipartFile file, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.registImage(file, userDetails);
    }

    // 유저 알림 리스트 조회
    @GetMapping("/api/user/alarm")
    public ResponseEntity<?>  getAlarmList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getAlarmList(userDetails.getUser());
    }

    // 1:1 매칭 상대, 수익률
    @GetMapping("/api/user/rival")
    public ResponseEntity<?> getRival(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getRival(userDetails.getUser());
    }

    // 퀘스트 리스트
    @GetMapping("/api/user/quest")
    public ResponseEntity<?> getQuest(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getQuest(userDetails.getUser());
    }

    

}


