package com.corinne.corinne_be.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class TimeUtil {

    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;


    public String calculateTime(LocalDateTime Date)
    {

        java.util.Date date = java.sql.Timestamp.valueOf(Date);

        long curTime = System.currentTimeMillis();
        long regTime = date.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;

        if (diffTime < SEC)
        {
            // sec
            msg = diffTime + "초전";
        }
        else if ((diffTime /= SEC) < MIN)
        {
            // min
            System.out.println(diffTime);

            msg = diffTime + "분전";
        }
        else if ((diffTime /= MIN) < HOUR)
        {
            // hour
            msg = (diffTime ) + "시간전";
        }
        else if ((diffTime /= HOUR) < DAY)
        {
            // day
            msg = (diffTime ) + "일전";
        }
        else if ((diffTime /= DAY) < MONTH)
        {
            // day
            msg = (diffTime ) + "달전";
        }
        else
        {
            msg = (diffTime) + "년전";
        }

        return msg;
    }
}
