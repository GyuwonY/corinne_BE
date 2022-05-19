package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.model.Alarm;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.AlarmRepository;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LevelUtil {

    private final AlarmRepository alarmRepository;

    @Autowired
    public LevelUtil(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public boolean levelUpCheck(User user, int exp){

        int preExp = user.getExp() - exp;

        String preLevel = levelCheck(preExp);
        String level = levelCheck(user.getExp());

        if(!preLevel.equals(level)){
            Alarm alarm = new Alarm(user, Alarm.AlarmType.LEVEL, level);
            alarmRepository.save(alarm);
            return true;
        }

        return false;
    }

    private String levelCheck(int exp){

        String level = "";
        if(exp < 5000){
            level = "레드";
        }
        else if(exp < 30000){
            level = "오렌지";
        }
        else if(exp < 60000){
            level = "옐로우";
        }
        else if(exp < 100000){
            level = "그린";
        }
        else if(exp < 200000){
            level = "스카이";
        }
        else if(exp < 350000){
            level = "네이비";
        }
        else {
            level = "퍼플";
        }

        return level;
    }
}























