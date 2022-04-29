package com.corinne.corinne_be.model;

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
    private int amount;

    public Coin() {
    }

    public Coin(User user, String tiker, double buyPrice, int amount) {
        this.user = user;
        this.tiker = tiker;
        this.buyPrice = buyPrice;
        this.amount = amount;
    }

    public void update(double buyPrice, int amount) {
        this.buyPrice = buyPrice;
        this.amount = amount;
    }

    public void update(int amount) {
        this.amount = amount;
    }
}
