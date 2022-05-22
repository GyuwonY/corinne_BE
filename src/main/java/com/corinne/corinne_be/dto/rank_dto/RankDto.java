package com.corinne.corinne_be.dto.rank_dto;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.Lint;

import java.util.List;

@Getter
@Setter
public class RankDto {

    private Long userId;
    private String nickname;
    private String imageUrl;
    private Long totalBalance;
    private int rank;
    private double fluctuationRate;
    private boolean follow;
    private int resetCount;
    private int exp;

    public RankDto() {
    }

    public RankDto(Long userId, String nickname, String imageUrl, Long totalBalance, double fluctuationRate, boolean follow, int resetCount, int exp) {
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.totalBalance = totalBalance;
        this.fluctuationRate = fluctuationRate;
        this.follow = follow;
        this.resetCount = resetCount;
        this.exp = exp;
    }
}
