package com.corinne.corinne_be.scheduler;

import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.RedisRepository;
import com.corinne.corinne_be.repository.TransactionRepository;
import com.corinne.corinne_be.repository.UserRepository;
import com.corinne.corinne_be.utils.RankUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

@Component
@EnableAsync
public class WeeklyScheduler {
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;
    private final TransactionRepository transactionRepository;
    private final RankUtil rankUtil;
    private final List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");

    @Autowired
    public WeeklyScheduler(RedisRepository redisRepository, UserRepository userRepository, TransactionRepository transactionRepository,
                           CoinRepository coinRepository, RankUtil rankUtil) {
        this.redisRepository = redisRepository;
        this.userRepository = userRepository;
        this.coinRepository = coinRepository;
        this.rankUtil = rankUtil;
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(cron = "0 0 0 ? * MON")
    @Transactional
    public void rankUpdate() {
        Random random = new Random();

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
            if(transactionRepository.countByUser_UserIdAndTradeAtBetween(user.getUserId(), startDate, endDate) != 0) {
                if (user.getRival() == 0) {
                    user.balanceUpdate(1000000L);
                    user.rivalUpdate(users.get(random.nextInt(userSize)).getUserId());
                } else {
                    double rivalFluctuation = userRepository.findByUserId(user.getRival()).orElseThrow(IllegalArgumentException::new).getLastFluctuation();
                    if (user.getLastFluctuation() > rivalFluctuation) {
                        user.balanceUpdate(1500000L);
                        user.expUpdate(10000);
                        user.rivalUpdate(users.get(random.nextInt(userSize)).getUserId());
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 5 0 ? * MON")
    @Transactional
    public void rewordUpdate() {
        userRepository.rankUpdate();
        userRepository.highRankUpdate();
        List<User> userList = userRepository.findTop3ByOrderByLastFluctuationDesc();
        for (User user : userList) {
            if (user.getLastRank() == 1) {
                user.balanceUpdate(1000000L);
            } else if (user.getLastRank() == 2) {
                user.balanceUpdate(700000L);
            } else if (user.getLastRank() == 3) {
                user.balanceUpdate(200000L);
            }
        }
    }

}
