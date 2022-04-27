package com.corinne.corinne_be.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MsgReponseDto {
    private  String msg;

    public MsgReponseDto(String msg){
        this.msg=msg;
    }
}
