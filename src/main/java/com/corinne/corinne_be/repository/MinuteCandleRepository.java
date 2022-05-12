package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.MinuteCandle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinuteCandleRepository extends JpaRepository<MinuteCandle, Long> {
    Page<MinuteCandle> findAllByTiker(String tiker,Pageable pageable);
    MinuteCandle findFirstByTiker(String tiker);
    Long countAllByTiker(String tiker);
}
