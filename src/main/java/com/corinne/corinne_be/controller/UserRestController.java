package com.corinne.corinne_be.controller;



import com.corinne.corinne_be.dto.MsgReponseDto;
import com.corinne.corinne_be.dto.user_dto.ProfileResponseDto;
import com.corinne.corinne_be.dto.user_dto.UserInfoResponesDto;
import com.corinne.corinne_be.dto.user_dto.UserRequestdto;
import com.corinne.corinne_be.dto.user_dto.UserResponesDto;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.service.KakaoService;
import com.corinne.corinne_be.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
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
    @GetMapping("/user/kakao/callback")
    public MsgReponseDto kakao(@RequestParam String code) throws JsonProcessingException {
        kakaoService.kakao(code);
        return new MsgReponseDto(HttpStatus.OK, null);
    }
    //회원정보조희
    @GetMapping("/api/user/info")
    public UserInfoResponesDto Userinfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.UserInfo(userDetails);
    }
    //회원정보 수정
    @PatchMapping("/user/signup")
    public MsgReponseDto InfoUpdate(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestdto userRequestdto){
        return userService.InfoUpdate(userDetails,userRequestdto);
    }

    //프로필이미지 수정
    @PatchMapping("/api/user/image")
    public ProfileResponseDto registImage(@RequestParam("image") MultipartFile file, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.registImage(file, userDetails);
    }
}


