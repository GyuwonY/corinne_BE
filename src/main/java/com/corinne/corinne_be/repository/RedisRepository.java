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
import org.springframework.transaction.annotation.Transactional;
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
    private ListOperations<String,Object> prices;
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
        prices = redisTemplate.opsForList();
        topics = new HashMap<>();
    }

    /**
     * 실시간 데이터 현재가 리턴
     */
    public PricePublishingDto getTradePrice(String tiker){
        return objectMapper.convertValue(tradePrice.get(tiker+"tradeprice"), PricePublishingDto.class);
    }

    public void saveBankruptcy(BankruptcyDto dto){
        BankruptcyDto checkDto = objectMapper.convertValue(prices.leftPop(dto.getTiker() + "bankruptcy"), BankruptcyDto.class);
        if(checkDto != null){
            prices.leftPush(dto.getTiker() + "bankruptcy", checkDto);

            for(int i = 0; i <= prices.size(dto.getTiker() + "bankruptcy").intValue(); i++){
                BankruptcyDto bankruptcyDto = objectMapper.convertValue(prices.leftPop(dto.getTiker() + "bankruptcy"), BankruptcyDto.class);
                if(bankruptcyDto.getCoinId().equals(dto.getCoinId())){
                    prices.rightPush(dto.getTiker() + "bankruptcy", dto);
                    break;
                }else{
                    prices.rightPush(dto.getTiker() + "bankruptcy", bankruptcyDto);
                    if(i==prices.size(dto.getTiker() + "bankruptcy").intValue()){
                        prices.rightPush(dto.getTiker() + "bankruptcy", dto);
                    }
                }
            }
        }else {
            prices.rightPush(dto.getTiker()+"bankruptcy", dto);
        }
    }

    public void deleteBankruptcy(Long coinId, String tiker) {
        BankruptcyDto checkDto = objectMapper.convertValue(prices.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
        if(checkDto != null){
            prices.leftPush(tiker + "bankruptcy", checkDto);
            for(Long i = 0L; i <= prices.size(tiker + "bankruptcy"); i++){
                BankruptcyDto bankruptcyDto = objectMapper.convertValue(prices.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
                if(bankruptcyDto.getCoinId().equals(coinId)){
                    break;
                }else{
                    prices.rightPush(tiker + "bankruptcy", bankruptcyDto);
                }
            }
        }
    }

    public void resetBankruptcy(Long userId) {
        List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");
        for (String tiker : tikers) {
            BankruptcyDto checkDto = objectMapper.convertValue(prices.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
            if (checkDto != null) {
                prices.leftPush(tiker + "bankruptcy", checkDto);
                for (Long i = 0L; i <= prices.size(tiker + "bankruptcy"); i++) {
                    BankruptcyDto bankruptcyDto = objectMapper.convertValue(prices.leftPop(tiker + "bankruptcy"), BankruptcyDto.class);
                    if (!bankruptcyDto.getUserId().equals(userId)) {
                        prices.rightPush(tiker + "bankruptcy", bankruptcyDto);
                    }
                }
            }
        }
    }

    public void deleteAllBankruptcy(){
        List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");
        for(String tiker : tikers){
            for(int i = 0; i<prices.size(tiker+"bankruptcy"); i++){
                prices.leftPop(tiker+"bankruptcy");
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
