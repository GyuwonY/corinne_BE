package com.corinne.corinne_be.model;

import com.corinne.corinne_be.dto.Quest_dto.RewordDto;
import com.corinne.corinne_be.dto.user_dto.UserRequestdto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
public class User {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long userId;

    @Column
    private String imageUrl = "null";

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long accountBalance = 1000000L;

    @Column(nullable = false)
    private int exp = 0;

    @Column(nullable = false)
    private boolean firstLogin = true;

    @Column(nullable = false)
    private double lastFluctuation;

    @Column(nullable = false)
    private int lastRank;

    @Column(nullable = false)
    private int highRank;

    @Column(nullable = false)
    private boolean alarm = false;

    @Column(nullable = false)
    private Long rival = 0L;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Coin> coin;

    @Version
    private Integer version;

    public User(String password, String userEmail) {
        this.password = password;
        this.userEmail = userEmail;
    }

    //회원정보 수정
    public void infoUpdate(UserRequestdto userRequestdto){
        this.nickname = userRequestdto.getNickname();
        this.firstLogin = false;
    }

    public void balanceUpdate(Long balance){
        this.accountBalance = balance;
    }

    //프로필 이미지 수정
    public void profileImgUpdate(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void lastFluctuationUpdate(double lastFluctuation){
        this.lastFluctuation = lastFluctuation;
    }

    public void rivalUpdate(Long rivalId, Long accountBalance){
        this.rival = rivalId;
        this.accountBalance = accountBalance;
    }

    public void rivalUpdate(Long rivalId, Long accountBalance, int exp){
        this.rival = rivalId;
        this.accountBalance = accountBalance;
        this.exp += exp;
    }

    public void expUpdate(int exp){
        this.exp += exp;
    }

    public void addBalance(Long reword){
        this.accountBalance += reword;
    }

    public void alarmUpdate(boolean alarm) {this.alarm = alarm; }

    public void rewordUpdate(RewordDto rewordDto){
        if(rewordDto.getAmount()!=null) {
            this.accountBalance += rewordDto.getAmount();
        }
        this.exp += rewordDto.getExp();
    }
}


