package com.corinne.corinne_be.repository;


import com.corinne.corinne_be.model.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findAllByUser_UserId(Long userId);
}
