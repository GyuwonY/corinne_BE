package com.corinne.corinne_be.service;

import com.corinne.corinne_be.model.Quest;
import com.corinne.corinne_be.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;

    @Transactional
    public void checkQuest(Long userId){
        Quest quest = questRepository.findByUser_UserIdAndQuestNo(userId, 6).orElse(null);

        if(quest != null){
            if(!quest.isClear()){
                quest.update(true);
            }
        }
    }
}
