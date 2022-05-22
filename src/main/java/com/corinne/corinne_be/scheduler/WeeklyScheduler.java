package com.corinne.corinne_be.scheduler;

import com.corinne.corinne_be.model.*;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.utils.LevelUtil;
import com.corinne.corinne_be.utils.RankUtil;
import com.corinne.corinne_be.websocket.RedisPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
    private final RankUtil rankUtil;
    private final LevelUtil levelUtil;
    private final RedisPublisher redisPublisher;
    private final QuestRepository questRepository;
    private final List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");

    @Autowired
    public WeeklyScheduler(RedisRepository redisRepository, UserRepository userRepository, TransactionRepository transactionRepository,
                           CoinRepository coinRepository, AlarmRepository alarmRepository, RankUtil rankUtil, LevelUtil levelUtil,
                           RedisPublisher redisPublisher, QuestRepository questRepository) {
        this.redisRepository = redisRepository;
        this.userRepository = userRepository;
        this.coinRepository = coinRepository;
        this.alarmRepository = alarmRepository;
        this.rankUtil = rankUtil;
        this.levelUtil = levelUtil;
        this.transactionRepository = transactionRepository;
        this.redisPublisher = redisPublisher;
        this.questRepository = questRepository;
    }

    @Scheduled(cron = "0 0 0 ? * MON")
    @Transactional
    public void rankUpdate() {

        List<User> users = userRepository.findAll();
        int userSize = users.size();

        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.DAY_OF_WEEK)==1){
            cal.add(Calendar.DATE, -1);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String mondayDate = dateFormat.format(cal.getTime());
        mondayDate += " 00:00:00.000";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime startDate = LocalDateTime.parse(mondayDate, formatter);
        LocalDateTime endDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);


        for(User user : users){
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            Long totalBalance = rankUtil.getTotalCoinBalance(coins) +  accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);

            //전주 수익률
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
                    // 배틀 결과 알림
                    Alarm alarm = new Alarm(user, Alarm.AlarmType.RIVAL, "승리");
                    alarmRepository.save(alarm);
                    // 레벨업 알림 체크
                    levelUtil.levelUpCheck(user, 10000);
                } else if(user.getLastFluctuation() < rivalFluctuation){
                    user.rivalUpdate(rival(users, user), 1000000L);
                    // 배틀 결과 알림
                    Alarm alarm = new Alarm(user, Alarm.AlarmType.RIVAL, "패배");
                    alarmRepository.save(alarm);
                } else {
                    user.rivalUpdate(rival(users, user), 1000000L);
                    Alarm alarm = new Alarm(user, Alarm.AlarmType.RIVAL, "무승부");
                    alarmRepository.save(alarm);
                }
            }

            if(transactionRepository.countByUser_UserIdAndTradeAtBetween(user.getUserId(), startDate, endDate) != 0) {
                // 주간 모의 투자 참여자 보상
                user.expUpdate(5000);
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "주간 랭킹 참여자 보상");
                alarmRepository.save(alarm);

                Quest quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 8).orElse(null);
                if(quest != null){
                    if(!quest.isClear()){
                        quest.update(true);
                    }
                }

                // 레벨업 알림 체크
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
                // 랭킹 보상 알림
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "랭킹 1위");
                alarmRepository.save(alarm);
                // 레벨업 알림 체크
                levelUtil.levelUpCheck(user, 30000);
            } else if (user.getLastRank() == 2) {
                user.addBalance(500000L);
                user.expUpdate(20000);
                // 랭킹 보상 알림
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "랭킹 2위");
                alarmRepository.save(alarm);
                // 레벨업 알림 체크
                levelUtil.levelUpCheck(user, 20000);
            } else if (user.getLastRank() == 3) {
                user.addBalance(200000L);
                user.expUpdate(10000);
                // 랭킹 보상 알림
                Alarm alarm = new Alarm(user, Alarm.AlarmType.RANK, "랭킹 3위");
                alarmRepository.save(alarm);
                // 레벨업 알림 체크
                levelUtil.levelUpCheck(user, 10000);
            }
            user.alarmUpdate(true);
        }

        List<User> users = userRepository.findAll();

        for(User user : users){
            Alarm alarm = new Alarm(user, Alarm.AlarmType.RESULT, user.getLastRank()+"위");
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
