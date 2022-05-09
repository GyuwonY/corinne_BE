package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.dto.coin_dto.PricePublishingDto;
import com.corinne.corinne_be.dto.transaction_dto.BankruptcyDto;
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
import java.util.HashMap;
import java.util.Map;

@Repository
public class RedisRepository {
    // Redis
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> tradePrice;
    private ListOperations<String,Object> bankruptcy;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        tradePrice = redisTemplate.opsForValue();
        bankruptcy = redisTemplate.opsForList();
    }

    /**
     * 실시간 데이터 현재가 리턴
     */
    public PricePublishingDto getTradePrice(String tiker){
        return objectMapper.convertValue(tradePrice.get(tiker+"tradeprice"), PricePublishingDto.class);
    }

    public void saveBankruptcy(BankruptcyDto dto){
        if(bankruptcy.size(dto.getTiker()+"bankruptcy") != null){
            for(Long i = 0L; i <= bankruptcy.size(dto.getTiker() + "bankruptcy"); i++){
                BankruptcyDto bankruptcyDto = objectMapper.convertValue(bankruptcy.leftPop(dto.getTiker() + "bankruptcy"), BankruptcyDto.class);
                if(bankruptcyDto.getCoinId().equals(dto.getCoinId())){
                    bankruptcy.rightPush(dto.getTiker() + "bankruptcy", dto);
                    break;
                }else{
                    bankruptcy.rightPush(dto.getTiker() + "bankruptcy", bankruptcyDto);
                }
            }
        }
    }

    public void deleteBankruptcy(Long coinId, String tiker) {
        if(bankruptcy.size(tiker+"bankruptcy") != null){
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
}
