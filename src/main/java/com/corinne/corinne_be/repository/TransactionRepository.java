package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAllByUser_UserId(Long userId, Pageable pageable);
    Page<Transaction> findAllByTikerAndUser_UserId(String coinName,Long userId, Pageable pageable);
}
