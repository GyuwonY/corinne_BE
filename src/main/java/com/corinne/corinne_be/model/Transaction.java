package com.corinne.corinne_be.model;

import com.corinne.corinne_be.dto.transaction_dto.TransactionDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tbl_transaction")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int buyprice;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String tiker;

    @Column(nullable = false)
    private int leverage;

    @CreatedDate
    private LocalDateTime tradeAt;

    public Transaction(TransactionDto transactionDto) {
        this.user = transactionDto.getUser();
        this.type = transactionDto.getType();
        this.buyprice = transactionDto.getPrice();
        this.amount = transactionDto.getAmount();
        this.tiker = transactionDto.getTiker();
        this.leverage = transactionDto.getLeverage();
    }

    public Transaction() {
    }

}