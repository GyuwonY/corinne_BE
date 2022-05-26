package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.util_dto.SearchTimeDto;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@Component
public class TimeUtil {

    public final int SEC = 60;
    public final int MIN = 60;
    public final int HOUR = 24;
    public final int DAY = 30;
    public final int MONTH = 12;


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

    public SearchTimeDto SearchTime(String time)
    {
        Calendar cal = Calendar.getInstance();

        // 지난주 일 경우
        if(time.equals("lastWeek")){
            cal.add(Calendar.DATE, -7);
        }

        if(cal.get(Calendar.DAY_OF_WEEK)==1){
            cal.add(Calendar.DATE, -1);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String mondayDate = dateFormat.format(cal.getTime());
        mondayDate += " 00:00:00.000";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime startDate = LocalDateTime.parse(mondayDate, formatter);
        LocalDateTime endDate = LocalDateTime.now();


        return new SearchTimeDto(startDate,endDate);
    }
}
