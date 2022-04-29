package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.DateCandle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DateCandleRepository extends JpaRepository<DateCandle, Long> {
    Page<DateCandle> findAll(Pageable pageable);

    List<DateCandle> findAllByDate(LocalDateTime date);

}
