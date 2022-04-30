package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UserTranResponseDto {

    private List<UserTransactionDto> userTranDtos;

    public UserTranResponseDto() {
    }

    public UserTranResponseDto(List<UserTransactionDto> userTranDtos) {
        this.userTranDtos = userTranDtos;
    }
}
