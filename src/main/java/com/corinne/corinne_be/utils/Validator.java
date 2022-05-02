package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.user_dto.UserRequestdto;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class Validator {
    private final UserRepository userRepository;


    @Autowired
    public Validator(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public void userValidate(UserRequestdto userRequestdto) throws IllegalArgumentException {
//        if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,16}$",userRequestdto.getPassword())){
//            throw new IllegalArgumentException("비밀번호는 영문자와 숫자를 포함 또는 4~16자입니다");
//        }

//        if(userRequestdto.getPassword().contains(userRequestdto.getUserEmail())){
//            throw new IllegalArgumentException("ID가 포함되지 않은 비밀번호를 사용해주세요.");
//        }
        if (userRepository.findByNickname(userRequestdto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }
        if(!Pattern.matches("^[ㄱ-ㅎ가-힣a-z0-9-_]{4,9}$",userRequestdto.getNickname())){
            System.out.println("tss");
            throw new IllegalArgumentException("닉네임은 특수문자를 제외한 4~9 자입니다");
        }
//        if (userRepository.findByUserEmail(userRequestdto.getUserEmail()).isPresent()) {
//            throw new IllegalArgumentException("중복된 아이디가 존재합니다.");
//        }
//
//        if(!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", userRequestdto.getUserEmail())){
//            throw new IllegalArgumentException("이메일 형식의 ID를 입력 해주세요.");
//        }
    }

//    public Page<PostListDto> overPages(List<PostListDto> boardsList, int start, int end, Pageable pageable, int page) {
//        Page<PostListDto> pages = new PageImpl<>(boardsList.subList(start, end), pageable, boardsList.size());
//        if(page > pages.getTotalPages()){
//            throw new IllegalArgumentException("요청할 수 없는 페이지 입니다.");
//        }
//        return pages;
//    }
//
//    public static void emptyComment(CommentRequestDto commentRequestDto) {
//        if(commentRequestDto.getComment() == null) {
//            throw new IllegalArgumentException("댓글을 입력하세요");
//        }
//    }
//
//    public void alreadyDelete(boolean favorite, String s) {
//        if(favorite){
//            throw new IllegalArgumentException(s);
//        }
//    }
//
//    public void sameContent(boolean board, String s) {
//        if(board){
//            throw new IllegalArgumentException(s);
//        }
//    }
//
//    public static void sameComment(CommentRequestDto requestDto) throws IllegalArgumentException {
//        if(requestDto.getComment() == null){
//            throw new IllegalArgumentException("수정된 내용이 없습니다.");
//        }
//    }

}