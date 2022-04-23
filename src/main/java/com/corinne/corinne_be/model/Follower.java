package com.corinne.corinne_be.model;

import lombok.Getter;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_follower")
public class Follower {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long followerId;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "follower")
    private User follower;

}
