package com.corinne.corinne_be.dto.coin_dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PricePublishingDto implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;
    private String tiker;
    private int tradePrice;
    private int tradeDate;
    private int tradeTime;
    private Long tradeVolume;
    private int highPrice;
    private int lowPrice;
    private int prevClosingPrice;
    private int signedChangePrice;
    private float signedChangeRate;

}
