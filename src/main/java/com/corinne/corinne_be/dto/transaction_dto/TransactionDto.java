package com.corinne.corinne_be.dto.transaction_dto;

import com.corinne.corinne_be.model.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionDto {
    private User user;
    private String type;
    private int price;
    private String tiker;

    public TransactionDto() {
    }

    public TransactionDto(User user, String type, int price, String tiker) {
        this.user = user;
        this.type = type;
        this.price = price;
        this.tiker = tiker;
    }
}
