package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.user_dto.KakaoUserInfoDto;
import com.corinne.corinne_be.model.Quest;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.QuestRepository;
import com.corinne.corinne_be.repository.UserRepository;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.security.jwt.JwtTokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoService {

    private final int QUEST_SIZE = 9;

    private final PasswordEncoder encode;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;

    @Transactional
    public ResponseEntity<Boolean> kakao(String code) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfoDto = getKakaoUserInfo(accessToken);

        String userEmail = kakaoUserInfoDto.getEmail();
        User kakaoUser = userRepository.findByUserEmail(userEmail)
                .orElse(null);
        if (kakaoUser == null) {
            String nickname = kakaoUserInfoDto.getNickname();
            String passwordCreate = UUID.randomUUID().toString();
            String password = encode.encode(passwordCreate);
            kakaoUser = userRepository.save(new User(nickname, password, userEmail));

            List<Quest> quests= new ArrayList<>();
            for(int i = 1; i <= QUEST_SIZE; i++){
                Quest quest = new Quest(kakaoUser,i, false);
                quests.add(quest);
            }
            questRepository.saveAll(quests);
        }

        String token = forceLogin(kakaoUser);
        HttpHeaders headers = new HttpHeaders();
        token =  "BEARER" + " " + token;
        headers.set("Authorization",token);

        return  ResponseEntity.ok()
                .headers(headers)
                .body(kakaoUser.isFirstLogin());
    }



    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        return (new KakaoUserInfoDto(id,nickname,email));
    }

    private static String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "5c1212aa842ed21acf635fca0c1ce494");
        body.add("redirect_uri", "https://corinne.kr/user/kakao/callback");
        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private String forceLogin(User kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return JwtTokenUtils.generateJwtToken(new UserDetailsImpl(kakaoUser));
    }
}
