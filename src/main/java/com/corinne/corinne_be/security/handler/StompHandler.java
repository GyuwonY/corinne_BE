package com.corinne.corinne_be.security.handler;


import com.corinne.corinne_be.security.jwt.HeaderTokenExtractor;
import com.corinne.corinne_be.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Configuration
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            jwtDecoder.decodeId(headerTokenExtractor.extract(accessor.getFirstNativeHeader("token")));
        }
        return message;
    }
}