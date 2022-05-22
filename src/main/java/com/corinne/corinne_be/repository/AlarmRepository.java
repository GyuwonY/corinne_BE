package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.dto.alarm_dto.AlarmQueryDto;
import com.corinne.corinne_be.model.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUser_UserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    Long countAllByUser_UserIdAndContent(Long userId, String content);

    @Query(nativeQuery = true,value = "select * from \n" +
            "(select user_id,count(*) as win from tbl_alarm where user_id = :userId  and content = '승리') a,\n" +
            "(select count(*) as lose from tbl_alarm where user_id = :userId  and content = '패배') b,\n" +
            "(select count(*) as draw from tbl_alarm where user_id = :userId  and content = '무승부') c;")
    AlarmQueryDto battleResult(@Param("userId") Long userId);
}
