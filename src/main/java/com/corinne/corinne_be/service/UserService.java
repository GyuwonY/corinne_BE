package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.Quest_dto.QuestRequestDto;
import com.corinne.corinne_be.dto.Quest_dto.RewordDto;
import com.corinne.corinne_be.dto.Quest_dto.RewordResponseDto;
import com.corinne.corinne_be.dto.alarm_dto.AlarmQueryDto;
import com.corinne.corinne_be.dto.user_dto.*;
import com.corinne.corinne_be.exception.CustomException;
import com.corinne.corinne_be.exception.ErrorCode;
import com.corinne.corinne_be.model.Alarm;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.Quest;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.s3.S3Uploader;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.utils.*;
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
    private final LevelUtil levelUtil;
    private final TimeUtil timeUtil;

    @Transactional
    public ResponseEntity<UserInfoResponesDto> userInfo(Long userId, User user){
        UserInfoResponesDto userInfoResponesDto = getUserInfo(userId);
        userInfoResponesDto.setFollow(followerRepository.existsByUser_UserIdAndFollower_UserId(user.getUserId(), userId));
        AlarmQueryDto alarmQueryDto = alarmRepository.battleResult(userId);
        userInfoResponesDto.setWin(alarmQueryDto.getWin());
        userInfoResponesDto.setDraw(alarmQueryDto.getDraw());
        userInfoResponesDto.setLose(alarmQueryDto.getLose());
        Long participationCount = alarmRepository.countAllByUser_UserIdAndContent(user.getUserId(),"주간 랭킹 참여자 보상");
        userInfoResponesDto.setParticipation(participationCount);
        return new ResponseEntity<>(userInfoResponesDto,HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<UserInfoResponesDto> userInfo(Long userId){

        return new ResponseEntity<>(getUserInfo(userId),HttpStatus.OK);
    }

    //회원정보 조희
    private UserInfoResponesDto getUserInfo(Long userId){
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

        return new UserInfoResponesDto(user, rankUtil.getMyRank(userId),
                transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(userId, "reset",startDate,endDate), followerRepository.countAllByUser(user),
                followerRepository.countAllByFollower(user));
    }

    /**
     * 회원정보 수정
     *
     */
    @Transactional
    public ResponseEntity<HttpStatus> infoUpdate(User user, UserRequestdto userRequestdto){
        validator.userValidate(userRequestdto);
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
    public ResponseEntity<RivalDto> getRival(User user) {
        Long rivalId = user.getRival();

        User rival = userRepository.findByUserId(rivalId).orElse(null);

        if(rival == null){
            throw new CustomException(ErrorCode.NON_EXIST_RIVAL);
        }

        // 라이벌 수익률
        List<Coin> rivalCoins = coinRepository.findAllByUser_UserId(rivalId);
        Long rivalTotalBalance = rankUtil.getTotalCoinBalance(rivalCoins) + rival.getAccountBalance();
        BigDecimal temp = new BigDecimal(rivalTotalBalance - 1000000);
        BigDecimal rateCal = new BigDecimal(10000);
        double rivalFluctuationRate = temp.divide(rateCal,2, RoundingMode.HALF_EVEN).doubleValue();


        RivalDto rivalDto = new RivalDto(rival.getNickname(), rival.getImageUrl(),rivalFluctuationRate);

        return new ResponseEntity<>(rivalDto,HttpStatus.OK);
    }


    // 퀘스트 리스트

    public ResponseEntity<List<QuestDto>> getQuest(User user) {
        List<Quest> quests = questRepository.findAllByUser_UserId(user.getUserId());

        List<QuestDto> questDtos = new ArrayList<>();
        for(Quest quest : quests){
            QuestDto questDto = new QuestDto(quest.getQuestNo(), quest.isClear());
            questDtos.add(questDto);
        }

        return new ResponseEntity<>(questDtos,HttpStatus.OK);
    }

    //퀘스트 보상 받기
    @Transactional
    public ResponseEntity<RewordResponseDto> reword(QuestRequestDto questRequestDto, User user){
        RewordDto rewordDto = RewordUtil.switchReword(questRequestDto.getQuestNo());
        User result = userRepository.findByUserId(user.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));
        result.rewordUpdate(rewordDto);
        questRepository.deleteByUser_UserIdAndQuestNo(user.getUserId(), questRequestDto.getQuestNo());
        if(levelUtil.levelUpCheck(result, rewordDto.getExp())){
            result.alarmUpdate(true);
        }

        return new ResponseEntity<>(new RewordResponseDto(result), HttpStatus.OK);
    }
}
