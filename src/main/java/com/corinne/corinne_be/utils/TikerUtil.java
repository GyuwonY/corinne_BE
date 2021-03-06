package com.corinne.corinne_be.utils;

import org.springframework.stereotype.Component;

@Component
public class TikerUtil {

    public String switchTiker(String tiker){
        switch (tiker){
            case "KRW-BTC":
                return "비트코인";
            case "KRW-SOL":
                return "솔라나";
            case "KRW-ETH":
                return "이더리움";
            case "KRW-XRP":
                return "리플";
            case "KRW-ADA":
                return "에이다";
            case "KRW-AVAX":
                return "아발란체";
            case "KRW-DOT":
                return "폴카닷";
            case "KRW-MATIC":
                return "폴리곤";
        }
        return "";
    }

}
