package com.corinne.corinne_be.dto.util_dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SearchTimeDto {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public SearchTimeDto() {
    }

    public SearchTimeDto(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
