package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.user_dto.*;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.UserRepository;
import com.corinne.corinne_be.s3.S3Uploader;
import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final Validator validator;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    //회원정보 조희
    public ResponseEntity<?> UserInfo(User user){

        return new ResponseEntity<>(new UserInfoResponesDto(user),HttpStatus.OK);
    }

    /**
     * 회원정보 수정
     *
     */
    @Transactional
    public ResponseEntity<?> InfoUpdate(User user, UserRequestdto userRequestdto){
        userRequestdto.setUserEmail(user.getUserEmail());
        try{
            validator.userValidate(userRequestdto);
        }catch (IllegalArgumentException e){
            String msg = e.getMessage();
            return new ResponseEntity<>(msg,HttpStatus.BAD_REQUEST);
        }
        user.infoUpdate(userRequestdto);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //이미지수정
    @Transactional
    public ProfileResponseDto registImage(MultipartFile file, UserDetailsImpl userDetails) throws IOException {
        Long userId = userDetails.getUser().getUserId();
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        String imgurl = s3Uploader.upload(file, "static");
        user.profileImgUpdate(imgurl);
        return new ProfileResponseDto(imgurl);
    }

}
