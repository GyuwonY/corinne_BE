package com.corinne.corinne_be.model;


import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_quest")
@EntityListeners(AuditingEntityListener.class)
public class Quest {

    @GeneratedValue (strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long questId;

    @Column (nullable = false)
    private int questNo;

    @Column (nullable = false)
    private boolean clear;
}
