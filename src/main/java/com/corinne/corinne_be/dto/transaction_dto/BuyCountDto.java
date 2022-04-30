package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

@Getter
public class BuyCountDto {

    private Long buyCount;

    public BuyCountDto() {
    }

    public BuyCountDto(Long buyCount) {
        this.buyCount = buyCount;
    }
}
