package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.account_dto.AccountResponseDto;
import com.corinne.corinne_be.model.ChatMessage;
import com.corinne.corinne_be.repository.RedisRepository;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.websocket.RedisPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class testController {
    private final RedisPublisher redisPublisher;
    private final RedisRepository redisRepository;

    @Autowired
    public testController(RedisPublisher redisPublisher, RedisRepository redisRepository){
        this.redisPublisher = redisPublisher;
        this.redisRepository = redisRepository;
    }

    @GetMapping("/api/test")
    public void getBalance(@AuthenticationPrincipal UserDetailsImpl userDetails){
        redisRepository.enterTopic(Long.toString(userDetails.getUser().getUserId()));
        redisPublisher.publish(redisRepository.getTopic(Long.toString(userDetails.getUser().getUserId())), new ChatMessage(
                ChatMessage.MessageType.ALARM, LocalDateTime.now().plusHours(9).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                Long.toString(userDetails.getUser().getUserId()))
        );
    }
}
