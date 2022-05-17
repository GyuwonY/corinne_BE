package com.corinne.corinne_be.dto.user_dto;

public class RivalDto {

    private String nickname;
    private String imageUrl;
    private double rivalFluctuationRate;
    private double myFluctuationRate;

    public RivalDto() {
    }

    public RivalDto(String nickname, String imageUrl, double rivalFluctuationRate, double myFluctuationRate) {
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.rivalFluctuationRate = rivalFluctuationRate;
        this.myFluctuationRate = myFluctuationRate;
    }
}
