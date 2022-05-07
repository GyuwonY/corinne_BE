package com.corinne.corinne_be.controller;


import com.corinne.corinne_be.model.ChatMessage;
import com.corinne.corinne_be.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final RedisRepository redisRepository;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if(ChatMessage.MessageType.ENTER.equals(message.getType())){
            // 메세지 보낸 사람의 ID를 통해 nickName을 찾아 넣어줘야함
            //  FindByuserId      message.getMessageSender();
            String nickname = "닉네임";
            message.setMessage(nickname + "님이 입장하셨습니다.");
        }

        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(redisRepository.getTopic("corinnechat"), message);
    }
}
