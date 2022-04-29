package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.candle_dto.DateReponseDto;
import com.corinne.corinne_be.model.DateCandle;
import com.corinne.corinne_be.model.MinuteCandle;
import com.corinne.corinne_be.repository.DateCandleRepository;
import com.corinne.corinne_be.repository.MinuteCandleRepository;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceService {

    private final MinuteCandleRepository minuteCandleRepository;
    private final DateCandleRepository dateCandleRepository;

    @Autowired
    public PriceService(MinuteCandleRepository minuteCandleRepository, DateCandleRepository dateCandleRepository) {
        this.minuteCandleRepository = minuteCandleRepository;
        this.dateCandleRepository = dateCandleRepository;
    }


    // 분봉 조회
    public ResponseEntity<?> getMinute(int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MinuteCandle> minuteCandles = minuteCandleRepository.findAll(pageable);

        return new ResponseEntity<>(minuteCandles, HttpStatus.OK);
    }


    // 일봉 조회
   public ResponseEntity<?> getdate(int page, int size, String sortBy) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DateCandle> dateCandles = dateCandleRepository.findAll(pageable);

        return new ResponseEntity<>(dateCandles, HttpStatus.OK);
    }

    // 일별 등락률 랭킹
    public ResponseEntity<?> getDateRank() {

        // 어제 날짜 구하기
        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, -1); //날짜를 하루 뺀다.
        String date = sdf.format(cal.getTime());
        date += " 00:00:00.000";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

        List<DateCandle> dateCandles = dateCandleRepository.findAllByDate(dateTime);

        List<DateReponseDto> dateReponseDtos = new ArrayList<>();

        for(DateCandle dateCandle : dateCandles){

            // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
            int currentTempPrice = 100;

            String coin = dateCandle.getTiker();

            int tradePrice = currentTempPrice;
            int endPrice = dateCandle.getEndPrice();

            BigDecimal fluctuationRateCal = new BigDecimal((tradePrice - endPrice) * 100);
            double fluctuationRate = fluctuationRateCal.divide(BigDecimal.valueOf(endPrice),2,RoundingMode.HALF_EVEN).doubleValue();

            DateReponseDto dateReponseDto = new DateReponseDto(coin,tradePrice,fluctuationRate);

            dateReponseDtos.add(dateReponseDto);
        }

        dateReponseDtos = dateReponseDtos.stream().sorted(Comparator.comparing(DateReponseDto::getFluctuationRate).reversed()).collect(Collectors.toList());


        return new ResponseEntity<>(dateReponseDtos,HttpStatus.OK);
    }
}
