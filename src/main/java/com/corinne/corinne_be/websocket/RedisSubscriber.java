package com.corinne.corinne_be.websocket;

import com.corinne.corinne_be.dto.socket_dto.ChatMessage;
import com.corinne.corinne_be.dto.socket_dto.PricePublishingDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber{

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            // redis에서 발행된 데이터를 받아 deserialize
            if(!publishMessage.contains("type")) {
                PricePublishingDto tradePrice = objectMapper.readValue(publishMessage, PricePublishingDto.class);
                messagingTemplate.convertAndSend("/sub/topic/" + tradePrice.getTiker(), tradePrice);
            }else {
                // ChatMessage 객채로 맵핑
                ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
                // Websocket 구독자에게 채팅 메시지 Send
                messagingTemplate.convertAndSend("/sub/topic/" + roomMessage.getTopicName(), roomMessage);
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
