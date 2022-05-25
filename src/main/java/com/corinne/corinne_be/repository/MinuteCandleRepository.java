package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.MinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MinuteCandleRepository extends JpaRepository<MinuteCandle, Long> {
    List<MinuteCandle> findAllByTiker(String tiker);
}
