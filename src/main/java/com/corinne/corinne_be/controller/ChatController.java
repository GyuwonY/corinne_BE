package com.corinne.corinne_be.controller;

import com.corinne.corinne_be.dto.socket_dto.ChatMessage;
import com.corinne.corinne_be.repository.RedisRepository;
import com.corinne.corinne_be.service.QuestService;
import com.corinne.corinne_be.utils.AlarmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {
    private final QuestService questService;
    private final AlarmUtil alarmUtil;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if(ChatMessage.MessageType.ENTER.equals(message.getType())){
            message.setMessage(message.getNickname() + "님이 입장하셨습니다.");
        }else if(!message.isClear() && ChatMessage.MessageType.TALK.equals(message.getType())){
            questService.checkQuest(message.getUserId());
            alarmUtil.sendAlarm(message.getUserId().toString());
        }

        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
