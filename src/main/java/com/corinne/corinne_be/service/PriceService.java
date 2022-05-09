package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.candle_dto.DatePageDto;
import com.corinne.corinne_be.dto.candle_dto.DateReponseDto;
import com.corinne.corinne_be.dto.candle_dto.MinutePageDto;
import com.corinne.corinne_be.dto.transaction_dto.TransactionResponseDto;
import com.corinne.corinne_be.model.DateCandle;
import com.corinne.corinne_be.model.MinuteCandle;
import com.corinne.corinne_be.model.Transaction;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.BookmarkRepository;
import com.corinne.corinne_be.repository.DateCandleRepository;
import com.corinne.corinne_be.repository.MinuteCandleRepository;
import com.corinne.corinne_be.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PriceService {

    private final MinuteCandleRepository minuteCandleRepository;
    private final DateCandleRepository dateCandleRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RedisRepository redisRepository;

    @Autowired
    public PriceService(MinuteCandleRepository minuteCandleRepository, DateCandleRepository dateCandleRepository, BookmarkRepository bookmarkRepository, RedisRepository redisRepository) {
        this.minuteCandleRepository = minuteCandleRepository;
        this.dateCandleRepository = dateCandleRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.redisRepository = redisRepository;
    }





    // 분봉 조회
    public ResponseEntity<?> getMinute(String tikerName,int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MinuteCandle> entites = minuteCandleRepository.findAllByTiker(tikerName,pageable);
        Page<MinutePageDto> minutePageDtos = entites.map(minuteCandle -> {
            String tiker = minuteCandle.getTiker();
            int startPrice = minuteCandle.getStartPrice();
            int endPrice = minuteCandle.getEndPrice();
            int highPrice = minuteCandle.getHighPrice();
            int lowPrice = minuteCandle.getLowPrice();

            String date =String.valueOf(minuteCandle.getTradeDate());
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date formatDate = null;
            try {
                formatDate = dtFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String tradeDate  = newDtFormat.format(formatDate);

            int time = minuteCandle.getTradeTime();
            String tradeTime = "";
            if(time/100 >= 10){
                tradeTime += time/100 + ":" + time%100;
            } else {
                tradeTime += "0" + time/100 + ":" + time%100;
            }
            return new MinutePageDto(tiker,startPrice,endPrice,highPrice,lowPrice,tradeDate,tradeTime);
        });
        return new ResponseEntity<>(minutePageDtos, HttpStatus.OK);
    }


    // 일봉 조회
   public ResponseEntity<?> getdate(String tikerName, int page, int size, String sortBy) {
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DateCandle> entites = dateCandleRepository.findAllByTiker(tikerName, pageable);
        Page<DatePageDto> dateCandles = entites.map(new Function<DateCandle, DatePageDto>() {
            @Override
            public DatePageDto apply(DateCandle dateCandle) {
                String tiker = dateCandle.getTiker();
                int startPrice = dateCandle.getStartPrice();
                int endPrice = dateCandle.getEndPrice();
                int highPrice = dateCandle.getHighPrice();
                int lowPrice = dateCandle.getLowPrice();

                String date = String.valueOf(dateCandle.getTradeDate());
                SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date formatDate = null;
                try {
                    formatDate = dtFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String tradeDate = newDtFormat.format(formatDate);

                return new DatePageDto(tiker, startPrice, endPrice, highPrice, lowPrice, tradeDate);
            }
        });
       return new ResponseEntity<>(dateCandles, HttpStatus.OK);
    }


    // 일별 등락률 랭킹
    public ResponseEntity<?> getDateRank(User user) {

        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, -1); // 오늘날짜로부터 -1
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 날짜 포맷

        String yesterDate = sdf.format(date.getTime()); // String으로 저장
        List<DateCandle> dateCandles = dateCandleRepository.findAllByTradeDate(Integer.parseInt(yesterDate));

        List<DateReponseDto> dateReponseDtos = new ArrayList<>();

        // 어제 일봉 비교
        for(DateCandle dateCandle : dateCandles){

            // 현재가
            int currentPrice = redisRepository.getTradePrice(dateCandle.getTiker());

            String tiker = dateCandle.getTiker();

            String tikername = "";
            switch (tiker){
                case "KRW-BTC":
                    tikername = "비트코인";
                    break;
                case "KRW-SOL":
                    tikername = "솔라나";
                    break;
                case "KRW-ETH":
                    tikername = "이더리움";
                    break;
                case "KRW-XRP":
                    tikername = "스텔라루멘";
                    break;
                case "KRW-ADA":
                    tikername = "에이다";
                    break;
                case "KRW-DOGE":
                    tikername = "도지코인";
                    break;
                case "KRW-AVAX":
                    tikername = "아발란체";
                    break;
                case "KRW-DOT":
                    tikername = "폴카닷";
                    break;
            }

            // 어제 일봉 종료값
            int endPrice = dateCandle.getEndPrice();

            BigDecimal fluctuationRateCal = new BigDecimal((currentPrice - endPrice) * 100);
            double fluctuationRate = fluctuationRateCal.divide(BigDecimal.valueOf(endPrice),2,RoundingMode.HALF_EVEN).doubleValue();

            // 코인단위
            String unit = tiker.substring(4);

            // 즐겨찾기 유무
            boolean favorite = bookmarkRepository.existsByUserIdAndTiker(user.getUserId(), tiker);

            DateReponseDto dateReponseDto = new DateReponseDto(tiker,tikername, endPrice, fluctuationRate,unit,favorite);

            dateReponseDtos.add(dateReponseDto);
        }

        // 등락률에 맞춰 정렬
        dateReponseDtos = dateReponseDtos.stream().sorted(Comparator.comparing(DateReponseDto::getFluctuationRate).reversed()).collect(Collectors.toList());


        return new ResponseEntity<>(dateReponseDtos,HttpStatus.OK);
    }
}
