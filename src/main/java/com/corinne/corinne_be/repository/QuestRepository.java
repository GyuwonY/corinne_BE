package com.corinne.corinne_be.repository;


import com.corinne.corinne_be.model.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findAllByUser_UserId(Long userId);

    Optional<Quest> findByUser_UserIdAndQuestNo(Long userId, int questNo);
}
