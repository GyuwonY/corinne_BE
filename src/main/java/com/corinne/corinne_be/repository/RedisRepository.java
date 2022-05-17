package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.dto.coin_dto.PricePublishingDto;
import com.corinne.corinne_be.dto.transaction_dto.BankruptcyDto;
import com.corinne.corinne_be.websocket.RedisPublisher;
import com.corinne.corinne_be.websocket.RedisSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RedisRepository {
    // Redis
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> tradePrice;
    private ListOperations<String,Object> bankruptcy;
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    private final RedisPublisher redisPublisher;
    private Map<String, ChannelTopic> topics;


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public RedisRepository(RedisTemplate<String, Object> redisTemplate, RedisMessageListenerContainer redisMessageListener,
                           RedisSubscriber redisSubscriber, RedisPublisher redisPublisher) {
        this.redisTemplate = redisTemplate;
        this.redisMessageListener = redisMessageListener;
        this.redisSubscriber = redisSubscriber;
        this.redisPublisher = redisPublisher;
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
    public PricePublishingDto getTradePrice(String tiker){
        return objectMapper.convertValue(tradePrice.get(tiker+"tradeprice"), PricePublishingDto.class);
    }

    public void saveBankruptcy(BankruptcyDto dto){
        System.out.println(dto + "세이브");
        BankruptcyDto checkDto = objectMapper.convertValue(bankruptcy.leftPop(dto.getTiker() + "bankruptcy"), BankruptcyDto.class);
        if(checkDto != null){
            System.out.println("null 체크여부");
            bankruptcy.leftPush(dto.getTiker() + "bankruptcy", checkDto);
            for(Long i = 0L; i <= bankruptcy.size(dto.getTiker() + "bankruptcy"); i++){
                BankruptcyDto bankruptcyDto = objectMapper.convertValue(bankruptcy.leftPop(dto.getTiker() + "bankruptcy"), BankruptcyDto.class);
                if(bankruptcyDto.getCoinId().equals(dto.getCoinId())){
                    bankruptcy.rightPush(dto.getTiker() + "bankruptcy", dto);
                    break;
                }else{
                    bankruptcy.rightPush(dto.getTiker() + "bankruptcy", bankruptcyDto);
                }
            }
        }else {
            bankruptcy.rightPush(dto.getTiker() + "bankruptcy", dto);
            System.out.println(dto + "저장완료");
        }
    }

    public void deleteBankruptcy(Long coinId, String tiker) {
        BankruptcyDto checkDto = objectMapper.convertValue(bankruptcy.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
        if(checkDto != null){
            bankruptcy.leftPush(tiker + "bankruptcy", checkDto);
            for(Long i = 0L; i <= bankruptcy.size(tiker + "bankruptcy"); i++){
                BankruptcyDto bankruptcyDto = objectMapper.convertValue(bankruptcy.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
                if(bankruptcyDto.getCoinId().equals(coinId)){
                    break;
                }else{
                    bankruptcy.rightPush(tiker + "bankruptcy", bankruptcyDto);
                }
            }
        }
    }

    public void resetBankruptcy(Long userId) {
        List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");
        for (String tiker : tikers) {
            BankruptcyDto checkDto = objectMapper.convertValue(bankruptcy.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
            if (checkDto != null) {
                bankruptcy.leftPush(tiker + "bankruptcy", checkDto);
                for (Long i = 0L; i <= bankruptcy.size(tiker + "bankruptcy"); i++) {
                    BankruptcyDto bankruptcyDto = objectMapper.convertValue(bankruptcy.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
                    if (!bankruptcyDto.getUserId().equals(userId)) {
                        bankruptcy.rightPush(tiker + "bankruptcy", bankruptcyDto);
                    }
                }
            }
        }
    }

    public void deleteAllBankruptcy(){
        List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");
        for(String tiker : tikers){
            for(int i = 0; i<bankruptcy.size(tiker+"bankruptcy"); i++){
                bankruptcy.leftPop(tiker+"bankruptcy");
            }
        }
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
