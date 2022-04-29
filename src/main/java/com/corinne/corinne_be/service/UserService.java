package com.corinne.corinne_be.service;


import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.UserRepository;
import com.corinne.corinne_be.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, Validator validator, PasswordEncoder encoder, S3Uploader s3Uploader){
        this.userRepository = userRepository;
        this.validator = validator;
        this.encoder = encoder;
        this.s3Uploader = s3Uploader;
    }

    // 회원가입
    public String signup(SignupRequestDto signupRequestDto) {
        String msg = "회원 가입이 완료되었습니다qwqwqqww.";

        try {
            // 회원가입 검증
            validator.signupValidate(signupRequestDto);

        }catch (IllegalArgumentException e){
            msg = e.getMessage();
            return msg;
        }

        signupRequestDto.setPassword(encoder.encode(signupRequestDto.getPassword()));
        userRepository.save(new User(signupRequestDto));
        return msg;
    }

    @Transactional
    public ProfileResponseDto registImage(MultipartFile file, UserDetailsImpl userDetails) throws IOException {
        Long userId = userDetails.getUser().getUserId();
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        UserImage userImage = new UserImage(s3Uploader.upload(file, "static"));
        userImageRepository.save(userImage);
        user.update(userImage);
        return new ProfileResponseDto(userImage);
    }
}
