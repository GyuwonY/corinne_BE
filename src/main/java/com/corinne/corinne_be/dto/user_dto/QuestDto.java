package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;

@Getter
public class QuestDto {
    private int questNo;
    private boolean clear;

    public QuestDto() {
    }

    public QuestDto(int questNo, boolean clear) {
        this.questNo = questNo;
        this.clear = clear;
    }
}
