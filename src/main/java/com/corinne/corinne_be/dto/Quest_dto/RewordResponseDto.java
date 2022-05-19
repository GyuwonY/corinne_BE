package com.corinne.corinne_be.dto.Quest_dto;

import com.corinne.corinne_be.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewordResponseDto {
    private int exp;
    private Long accountBalance;
    private boolean alarm;

    public RewordResponseDto(User user){
        this.exp = user.getExp();
        this.accountBalance = user.getAccountBalance();
        this.alarm = user.isAlarm();
    }
}
