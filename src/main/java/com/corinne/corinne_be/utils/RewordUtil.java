package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.Quest_dto.RewordDto;

public class RewordUtil {

    public RewordDto switchReword(int questNo){
        RewordDto rewordDto = new RewordDto();
        switch (questNo){
            case 1:
            case 2:
            case 8:
            case 9:
                rewordDto.setAmount(100000L);
                rewordDto.setExp(10000);
                break;
            case 3:
            case 4:
            case 5:
                rewordDto.setAmount(100000L);
                rewordDto.setExp(5000);
                break;
            case 6:
                rewordDto.setExp(5000);
                break;
            case 7:
                rewordDto.setExp(10000);
                break;

        }
        return rewordDto;
    }
}
