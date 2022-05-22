package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.user_dto.UserRequestdto;
import com.corinne.corinne_be.exception.CustomException;
import com.corinne.corinne_be.exception.ErrorCode;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class Validator {
    private final UserRepository userRepository;


    @Autowired
    public Validator(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void userValidate(UserRequestdto userRequestdto) throws IllegalArgumentException {
        if (userRepository.findByNickname(userRequestdto.getNickname()).isPresent()) {
            throw new CustomException(ErrorCode.EXIST_NICKNAME);
        }
        if (!Pattern.matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,6}$", userRequestdto.getNickname())) {
            throw new CustomException(ErrorCode.WRONG_VALUE_NICKNAME);
        }
    }
}