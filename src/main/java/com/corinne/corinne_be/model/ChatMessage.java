package com.corinne.corinne_be.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type; // 메시지 타입
    private String topicName;
    private String nickname; // 메시지 보낸사람
    private String imageUrl;
    private Long exp;
    private String message; // 메시지
}
