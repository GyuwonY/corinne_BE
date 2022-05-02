package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UserTranResponseDto {

    private List<TransactionResponseDto> content;

    public UserTranResponseDto() {
    }

    public UserTranResponseDto(List<TransactionResponseDto> tranDtos) {
        this. content = tranDtos;
    }
}
