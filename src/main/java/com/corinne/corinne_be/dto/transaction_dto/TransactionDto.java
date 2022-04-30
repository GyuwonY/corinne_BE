package com.corinne.corinne_be.dto.transaction_dto;

import com.corinne.corinne_be.model.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionDto {
    private User user;
    private String type;
    private int price;
    private Long amount;
    private String tiker;
    private int leverage;

    public TransactionDto() {
    }

    public TransactionDto(User user, String type, int price, Long amount, String tiker, int leverage) {
        this.user = user;
        this.type = type;
        this.price = price;
        this.amount = amount;
        this.tiker = tiker;
        this.leverage = leverage;
    }
}
