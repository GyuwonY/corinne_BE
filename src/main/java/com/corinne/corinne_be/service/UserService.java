package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.user_dto.*;
import com.corinne.corinne_be.model.Alarm;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.Quest;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.s3.S3Uploader;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.utils.RankUtil;
import com.corinne.corinne_be.utils.TimeUtil;
import com.corinne.corinne_be.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final Validator validator;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final RankUtil rankUtil;
    private final TransactionRepository transactionRepository;
    private final FollowerRepository followerRepository;
    private final AlarmRepository alarmRepository;
    private final CoinRepository coinRepository;
    private final QuestRepository questRepository;

    private final TimeUtil timeUtil;

    //회원정보 조희
    @Transactional
    public ResponseEntity<?> UserInfo(Long userId){
        User user = userRepository.findByUserId(userId).orElseThrow(IllegalArgumentException::new);


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
        LocalDateTime endDate = LocalDateTime.now();


        return new ResponseEntity<>(new UserInfoResponesDto(user, rankUtil.getMyRank(userId),
                transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(userId, "reset",startDate,endDate), followerRepository.countAllByUser(user),
                followerRepository.countAllByFollower(user)),HttpStatus.OK);
    }

    /**
     * 회원정보 수정
     *
     */
    @Transactional
    public ResponseEntity<?> InfoUpdate(User user, UserRequestdto userRequestdto){
        try{
            validator.userValidate(userRequestdto);
        }catch (IllegalArgumentException e){
            String msg = e.getMessage();
            return new ResponseEntity<>(msg,HttpStatus.OK);
        }
        user.infoUpdate(userRequestdto);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //이미지수정
    @Transactional
    public ProfileResponseDto registImage(MultipartFile file, UserDetailsImpl userDetails) throws IOException {
        Long userId = userDetails.getUser().getUserId();
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        String imgurl = s3Uploader.upload(file, "static");
        user.profileImgUpdate(imgurl);
        return new ProfileResponseDto(imgurl);
    }

    // 알림 리스트 조회
    public ResponseEntity<List<AlarmDto>> getAlarmList(User user) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(calendar.getTime());
        date += " 00:00:00.000";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime startDate = LocalDateTime.parse(date, formatter);
        LocalDateTime endDate = LocalDateTime.now();

        List<Alarm> alarmList =alarmRepository.findAllByUser_UserIdAndCreatedAtBetween(user.getUserId(),startDate,endDate);

        List<AlarmDto> alarmDtos = new ArrayList<>();
        for(Alarm alarm : alarmList){
            String createdAt = timeUtil.calculateTime(alarm.getCreatedAt());
            AlarmDto alarmDto = new AlarmDto(alarm.getAlarmNo(),alarm.getContent(),createdAt);
            alarmDtos.add(alarmDto);
        }

        // 알림 체크
        user.alarmUpdate(false);

        return new ResponseEntity<>(alarmDtos, HttpStatus.OK);
    }

    // 1:1 매칭 상대, 수익률
    public ResponseEntity<?> getRival(User user) {
        Long rivalId = user.getRival();

        User rival = userRepository.findByUserId(rivalId).orElse(null);

        if(rival == null){
            return new ResponseEntity<>("지정된 라이벌이 없습니다.",HttpStatus.BAD_REQUEST);
        }

        // 라이벌 수익률
        List<Coin> rivalCoins = coinRepository.findAllByUser_UserId(rivalId);
        Long rivalTotalBalance = rankUtil.getTotalCoinBalance(rivalCoins) + rival.getAccountBalance();
        BigDecimal temp = new BigDecimal(rivalTotalBalance - 1000000);
        BigDecimal rateCal = new BigDecimal(10000);
        double rivalFluctuationRate = temp.divide(rateCal,2, RoundingMode.HALF_EVEN).doubleValue();

        // 내 수익률
        List<Coin> myCoins = coinRepository.findAllByUser_UserId(user.getUserId());
        Long myTotalBalance = rankUtil.getTotalCoinBalance(rivalCoins) + rival.getAccountBalance();
        BigDecimal caltemp = new BigDecimal(myTotalBalance - 1000000);
        double myFluctuationRate = temp.divide(rateCal,2, RoundingMode.HALF_EVEN).doubleValue();

        RivalDto rivalDto = new RivalDto(rival.getNickname(), rival.getImageUrl(),rivalFluctuationRate,myFluctuationRate);

        return new ResponseEntity<>(rivalDto,HttpStatus.OK);
    }


    // 퀘스트 리스트
    public ResponseEntity<?> getQuest(User user) {
        List<Quest> quests = questRepository.findAllByUser_UserId(user.getUserId());

        List<QuestDto> questDtos = new ArrayList<>();
        for(Quest quest : quests){
            QuestDto questDto = new QuestDto(quest.getQuestNo(), quest.isClear());
            questDtos.add(questDto);
        }

        return new ResponseEntity<>(questDtos,HttpStatus.OK);
    }
}


























