package com.corinne.corinne_be.model;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
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
    @JoinColumn(name = "userid")
    private User user;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String coinName;

    @CreatedDate
    private LocalDateTime tradeAt;
}