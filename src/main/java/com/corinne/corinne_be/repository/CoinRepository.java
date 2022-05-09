package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoinRepository extends JpaRepository<Coin, Long> {

    List<Coin> findAllByUser_UserId(Long userId);
    Optional<Coin> findByTikerAndUser_UserIdAndLeverage(String tiker, Long userId, int leverage);
    Optional<Coin> findByTikerAndUser_UserId(String tiker, Long userId);
    void deleteByTikerAndUser_UserId(String tiker, Long userId);
    Optional<Coin> findByTiker(String tiker);

    void deleteAllByUser_UserId(Long userId);
}
