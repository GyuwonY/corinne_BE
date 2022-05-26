package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.socket_dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmUtil {
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;


    public void sendAlarm(String userId){
        ChatMessage alarm = new ChatMessage(ChatMessage.MessageType.ALARM, userId);
        redisTemplate.convertAndSend(channelTopic.getTopic(), alarm);
    }
}
