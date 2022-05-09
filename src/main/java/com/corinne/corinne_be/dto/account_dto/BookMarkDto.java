package com.corinne.corinne_be.dto.account_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookMarkDto {

    private String tiker;

    public BookMarkDto() {
    }

    public BookMarkDto(String tiker) {
        this.tiker = tiker;
    }
}
