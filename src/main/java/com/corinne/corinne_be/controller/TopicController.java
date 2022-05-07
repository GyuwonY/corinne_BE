package com.corinne.corinne_be.controller;


import com.corinne.corinne_be.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {
    private final RedisRepository redisRepository;

    @Autowired
    public TopicController(RedisRepository redisRepository){
        this.redisRepository = redisRepository;
    }

    // 모의투자 접속 시 채팅과 실시간 시세를 위한 Topic 생성 후 sub할 Topic return
    @GetMapping("/chart/{tiker}")
    public void insertTopic(@PathVariable String tiker){
//        redisRepository.enterTopic(tiker);
//        topicRequestDto = new TopicRequestDto(tiker+"chart", "chat");
//        return topicRequestDto;
    }
}
