package com.corinne.corinne_be.dto.user_dto;

import com.corinne.corinne_be.model.Alarm;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AlarmDto {

    private Alarm.AlarmType alarmNo;
    private String content;
    private String createdAt;

    public AlarmDto() {
    }

    public AlarmDto(Alarm.AlarmType alarmNo, String content, String createdAt) {
        this.alarmNo = alarmNo;
        this.content = content;
        this.createdAt = createdAt;
    }
}
