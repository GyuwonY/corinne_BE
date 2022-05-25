package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.DayCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DayCandleRepository extends JpaRepository<DayCandle, Long> {
    List<DayCandle> findAllByTikerOrderByTradeDateAsc(String tiker);
}
