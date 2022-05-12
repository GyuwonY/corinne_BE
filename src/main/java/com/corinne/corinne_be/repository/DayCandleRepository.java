package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.DayCandle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DayCandleRepository extends JpaRepository<DayCandle, Long> {
    Page<DayCandle> findAllByTiker(String tiker, Pageable pageable);

    List<DayCandle> findAllByTradeDate(int date);

}
