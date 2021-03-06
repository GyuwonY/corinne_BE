package com.corinne.corinne_be.scheduler;

import com.corinne.corinne_be.dto.util_dto.SearchTimeDto;
import com.corinne.corinne_be.model.*;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.utils.BalanceUtil;
import com.corinne.corinne_be.utils.LevelUtil;
import com.corinne.corinne_be.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Service
@EnableAsync
public class WeeklyScheduler {
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;
    private final TransactionRepository transactionRepository;
    private final AlarmRepository alarmRepository;
    private final LevelUtil levelUtil;
    private final QuestRepository questRepository;
    private final BalanceUtil balanceUtil;
    private final TimeUtil timeUtil;

    @Autowired
    public WeeklyScheduler(RedisRepository redisRepository, UserRepository userRepository, TransactionRepository transactionRepository,
                           CoinRepository coinRepository, AlarmRepository alarmRepository, LevelUtil levelUtil,
                           QuestRepository questRepository, BalanceUtil balanceUtil, TimeUtil timeUtil) {
        this.redisRepository = redisRepository;
        this.userRepository = userRepository;
        this.coinRepository = coinRepository;
        this.alarmRepository = alarmRepository;
        this.levelUtil = levelUtil;
        this.transactionRepository = transactionRepository;
        this.questRepository = questRepository;
        this.balanceUtil = balanceUtil;
        this.timeUtil = timeUtil;
    }

    @Scheduled(cron = "0 0 0 ? * MON")
    @Transactional
    public void rankUpdate() {

        List<User> users = userRepository.findAll();


        SearchTimeDto date = timeUtil.SearchTime("thisWeek");

        for(User user : users){
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // ?????? ????????? ??????
            Long totalBalance = balanceUtil.totalCoinBalance(coins).getTotalcoinBalance() +  accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);

            //?????? ?????????
            double fluctuationRate = temp.divide(rateCal,2, RoundingMode.HALF_EVEN).doubleValue();
            user.lastFluctuationUpdate(fluctuationRate);
        }

        coinRepository.deleteAll();
        redisRepository.deleteAllBankruptcy();
        for(User user : users){
            if (user.getRival() == 0) {
                user.rivalUpdate(rival(users, user), 1000000L);
            }else {
                double rivalFluctuation = userRepository.findByUserId(user.getRival()).orElseThrow(IllegalArgumentException::new).getLastFluctuation();
                if (user.getLastFluctuation() > rivalFluctuation) {
                    user.rivalUpdate(rival(users, user), 1500000L, 10000);
                    // ?????? ?????? ??????
                    Alarm alarm = new Alarm(user, Alarm.AlarmType.RIVAL, "??????");
                    alarmRepository.save(alarm);
                    // ????????? ?????? ??????
                    levelUtil.levelUpCheck(user, 10000);
                } else if(user.getLastFluctuation() < rivalFluctuation){
                    user.rivalUpdate(rival(users, user), 1000000L);
                    // ?????? ?????? ??????
                    Alarm alarm = new Alarm(user, Alarm.AlarmType.RIVAL, "??????");
                    alarmRepository.save(alarm);
                } else {
                    user.rivalUpdate(rival(users, user), 1000000L);
                    Alarm alarm = new Alarm(user, Alarm.AlarmType.RIVAL, "?????????");
                    alarmRepository.save(alarm);
                }
            }

            if(transactionRepository.countByUser_UserIdAndTradeAtBetween(user.getUserId(), date.getStartDate(), date.getEndDate()) != 0) {
                // ?????? ?????? ?????? ????????? ??????
                user.expUpdate(5000);
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "?????? ?????? ????????? ??????");
                alarmRepository.save(alarm);

                Quest quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 8).orElse(null);
                if(quest != null){
                    if(!quest.isClear()){
                        quest.update(true);
                    }
                }

                // ????????? ?????? ??????
                if(levelUtil.levelUpCheck(user, 5000)){
                    user.alarmUpdate(true);
                }
            }
        }
    }

    @Scheduled(cron = "0 5 0 ? * MON")
    @Transactional
    public void rewordUpdate() {
        userRepository.rankUpdate();
        userRepository.highRankUpdate();
        List<User> userList = userRepository.findTop3ByLastFluctuationNotOrderByLastFluctuationDesc(0.0);

        for (User user : userList) {
            if (user.getLastRank() == 1) {
                user.addBalance(1000000L);
                user.expUpdate(30000);
                // ?????? ?????? ??????
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "?????? 1???");
                alarmRepository.save(alarm);
                // ????????? ?????? ??????
                levelUtil.levelUpCheck(user, 30000);
            } else if (user.getLastRank() == 2) {
                user.addBalance(500000L);
                user.expUpdate(20000);
                // ?????? ?????? ??????
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "?????? 2???");
                alarmRepository.save(alarm);
                // ????????? ?????? ??????
                levelUtil.levelUpCheck(user, 20000);
            } else if (user.getLastRank() == 3) {
                user.addBalance(200000L);
                user.expUpdate(10000);
                // ?????? ?????? ??????
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "?????? 3???");
                alarmRepository.save(alarm);
                // ????????? ?????? ??????
                levelUtil.levelUpCheck(user, 10000);
            }
            user.alarmUpdate(true);
        }

        List<User> users = userRepository.findAll();

        for(User user : users){
            Alarm alarm = new Alarm(user, Alarm.AlarmType.RESULT, user.getLastRank()+"???");
            alarmRepository.save(alarm);
            user.alarmUpdate(true);
        }
    }

    private Long rival(List<User> users, User user){
        Random random = new Random();
        Long rival = users.get(random.nextInt(users.size())).getUserId();
        if(rival == user.getUserId()){
            return rival(users, user);
        }
        return rival;
    }
}
