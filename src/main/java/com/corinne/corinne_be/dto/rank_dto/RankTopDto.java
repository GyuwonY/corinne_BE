package com.corinne.corinne_be.dto.rank_dto;

import com.corinne.corinne_be.model.User;
import lombok.Getter;

import java.util.List;

@Getter
public class RankTopDto {
    private List<RankInfoDto> rank;

    public RankTopDto() {
    }

    public RankTopDto(List<RankInfoDto> rank) {
        this.rank = rank;
    }


}
