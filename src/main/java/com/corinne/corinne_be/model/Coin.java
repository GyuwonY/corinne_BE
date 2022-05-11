package com.corinne.corinne_be.model;

import com.corinne.corinne_be.dto.transaction_dto.BuyRequestDto;
import lombok.Getter;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_coin")
public class Coin {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long coinId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private String tiker;

    @Column(nullable = false)
    private double buyPrice;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private int leverage;

    @Version
    private Integer version;

    public Coin() {
    }

    public Coin(User user, BuyRequestDto dto) {
        this.user = user;
        this.tiker = dto.getTiker();
        this.buyPrice = dto.getTradePrice();
        this.amount = dto.getBuyAmount();
        this.leverage = dto.getLeverage();
    }

    public void update(double buyPrice, Long amount) {
        this.buyPrice = buyPrice;
        this.amount = amount;
    }

    public void update(Long amount) {
        this.amount = amount;
    }
}
