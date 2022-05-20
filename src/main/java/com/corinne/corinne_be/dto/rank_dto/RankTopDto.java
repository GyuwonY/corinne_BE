package com.corinne.corinne_be.dto.rank_dto;

import com.corinne.corinne_be.model.User;
import lombok.Getter;

import java.util.List;

@Getter
public class RankTopDto {
    private List<User> rank;

    public RankTopDto() {
    }

    public RankTopDto(List<User> rank) {
        this.rank = rank;
    }


}
