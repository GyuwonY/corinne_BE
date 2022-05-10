package com.corinne.corinne_be.dto.follow_dto;

import com.corinne.corinne_be.dto.user_dto.UserInfoResponesDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class FollowDto {
    private Long userId;
    private String nickname;
    private int exp;
    private int rank;
    private double fluctuationRate;
    private String imageUrl;
    private Long totalBalance;

    public FollowDto(Long userId, String nickname, int exp, int rank, double fluctuationRate, String imageUrl, Long totalBalance) {
        this.userId = userId;
        this.nickname = nickname;
        this.exp = exp;
        this.rank = rank;
        this.fluctuationRate = fluctuationRate;
        this.imageUrl = imageUrl;
        this.totalBalance = totalBalance;
    }
}
