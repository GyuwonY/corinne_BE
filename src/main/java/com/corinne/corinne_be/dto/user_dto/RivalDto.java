package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RivalDto {

    private String nickname;
    private String imageUrl;
    private double rivalFluctuationRate;

    public RivalDto() {
    }

    public RivalDto(String nickname, String imageUrl, double rivalFluctuationRate) {
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.rivalFluctuationRate = rivalFluctuationRate;
    }
}
