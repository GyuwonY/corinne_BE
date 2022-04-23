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
    @JoinColumn(name = "userid")
    private User user;

    @Column(nullable = false)
    private String coinName;

    @Column(nullable = false)
    private Long buyPrice;

    @Column(nullable = false)
    private Long amount;
}
