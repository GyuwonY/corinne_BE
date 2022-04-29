package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoinRepository extends JpaRepository<Coin, Long> {

    List<Coin> findAllByUser_UserId(Long userId);
    Optional<Coin> findByCoinNameAndUser_UserId(String coinName, Long userId);
    Optional<Coin> findByCoinName(String coinName);
}
