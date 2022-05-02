package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.controller.RedisSubscriber;
import com.corinne.corinne_be.dto.transaction_dto.BankruptcyDto;
import com.corinne.corinne_be.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RedisRepository {
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;

    // Redis
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> tradePrice;
    private ListOperations<String,Object> bankruptcy;
    private Map<String, ChannelTopic> topics;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public RedisRepository(RedisMessageListenerContainer redisMessageListener, RedisTemplate<String, Object> redisTemplate,
                           RedisSubscriber redisSubscriber) {
        this.redisMessageListener = redisMessageListener;
        this.redisTemplate = redisTemplate;
        this.redisSubscriber = redisSubscriber;
    }

    @PostConstruct
    private void init() {
        tradePrice = redisTemplate.opsForValue();
        bankruptcy = redisTemplate.opsForList();
        topics = new HashMap<>();
    }

    /**
     * 실시간 데이터 현재가 리턴
     */
    public int getTradePrice(String tiker){
        return objectMapper.convertValue(tradePrice.get(tiker+"tradeprice"), Integer.class);
    }

    public void saveBankruptcy(BankruptcyDto dto){
        bankruptcy.rightPush(dto.getTiker()+"bankruptcy", dto);
    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public void enterChatRoom(ChatMessage message) {
        ChannelTopic topic = topics.get(message.getTopicName());

        if (topic == null) {
            topic = new ChannelTopic(message.getTopicName());
        }

        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(message.getTopicName(), topic);
    }

    public void enterTopic(String topicName) {
        ChannelTopic topic = topics.get(topicName);

        if (topic == null) {
            topic = new ChannelTopic(topicName);
        }

        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(topicName, topic);
    }

    public ChannelTopic getTopic(String topicName) {

        return topics.get(topicName);
    }

}
