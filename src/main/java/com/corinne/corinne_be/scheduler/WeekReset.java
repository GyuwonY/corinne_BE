package com.corinne.corinne_be.scheduler;

import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeekReset {

    private final UserRepository userRepository;
    private final CoinRepository coinRepository;

    @Autowired
    public WeekReset(UserRepository userRepository, CoinRepository coinRepository) {
        this.userRepository = userRepository;
        this.coinRepository = coinRepository;
    }
    // 주간 리셋 스케줄러
//    @Scheduled(cron = "0 * * * * *")
//    public void reset() {
//
//        System.out.println("reset Schedule move");
//
//        List<User> userList = userRepository.findAll();
//
//        List<RankDto> rankDtos = new ArrayList<>();
//
//        for(User user : userList){
//
//            int totalBalance = 0;
//            int accountBalance = user.getAccountBalance();
//
//            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());
//
//            // 보유 코인별 계산
//            for(Coin coin : coins){
//                // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
//                int currentTempPrice = 100;
//
//                // 현재 보유 코인값 계산  수정 필수
//                BigDecimal temp = new BigDecimal(coin.getAmount() * currentTempPrice);
//                BigDecimal buyPrice = new BigDecimal(coin.getBuyPrice());
//                BigDecimal currentPrice = temp.divide(buyPrice, RoundingMode.HALF_EVEN);
//
//
//                totalBalance += currentPrice.intValue();
//            }
//
//            totalBalance += accountBalance;
//
//            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
//            BigDecimal rateCal = new BigDecimal(10000);
//            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();
//
//
//            RankDto rankDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate);
//            rankDtos.add(rankDto);
//        }
//
//        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankDto::getTotalBalance).reversed()).collect(Collectors.toList());
//
//        for(User user : userList){
//            user.update(100);
//        }
//
//        userRepository.saveAll(userList);
//
//        for(int i = 0; i < 3; i++){
//            User saveUser = userRepository.findById(rankDtos.get(i).getUserId()).orElse(null);
//
//            if(saveUser == null){
//                reset();
//                break;
//            }
//
//            int temp = 0;
//            switch (i){
//                case 0: temp = 200;
//                    break;
//                case 1: temp = 170;
//                    break;
//                case 2: temp = 120;
//                    break;
//            }
//            saveUser.update(temp);
//            userRepository.save(saveUser);
//        }
//
//
//    }
//
}
