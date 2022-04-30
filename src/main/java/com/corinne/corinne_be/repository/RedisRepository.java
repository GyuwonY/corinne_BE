package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.dto.transaction_dto.BankruptcyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;

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
    public Long getTradePrice(String tiker){
        return objectMapper.convertValue(tradePrice.get(tiker+"tradeprice"), Long.class);
    }

    public void saveBankruptcy(BankruptcyDto dto){
        bankruptcy.rightPush(dto.getTiker()+"bankruptcy", dto);
    }

}
