package com.corinne.corinne_be.handler;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Configuration
public class StompHandler implements ChannelInterceptor {

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            //sessindId를 통해 현재 접속중인 유저 목록에서 삭제 시킨다.

        }
        return message;
    }
}