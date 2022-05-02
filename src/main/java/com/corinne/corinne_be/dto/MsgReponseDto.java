package com.corinne.corinne_be.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class MsgReponseDto {
    private HttpStatus status;
    private  String msg;

    public MsgReponseDto(HttpStatus status, String msg){
        this.status=status;
        this.msg=msg;
    }
}
