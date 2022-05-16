package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAllByUser_UserId(Long userId, Pageable pageable);
    Page<Transaction> findAllByTikerAndUser_UserId(String coinName,Long userId, Pageable pageable);

    List<Transaction> findTop5ByUser_UserIdOrderByTradeAtDesc(Long userId);
    Long countByTikerAndTypeAndTradeAtBetween(String tiker, String type, LocalDateTime startDate, LocalDateTime endDate);
    Long countByUser_UserIdAndType(Long userId, String type);
    Long countByUserIdAndTradeAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
