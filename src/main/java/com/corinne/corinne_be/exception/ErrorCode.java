package com.corinne.corinne_be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EXIST_BOOKMARK(HttpStatus.BAD_REQUEST.value(), "1","이미 즐겨찾기 등록된 종목입니다."),
    NON_EXIST_BOOKMARK(HttpStatus.BAD_REQUEST.value(), "2","즐겨찾기 등록되지 않은 종목입니다."),
    WRONG_TARGET_FOLLOW(HttpStatus.BAD_REQUEST.value(), "3","자신을 팔로우할 수 없습니다."),
    NON_EXIST_USER(HttpStatus.BAD_REQUEST.value(), "4","존재하지 않는 유저입니다."),
    WRONG_VALUE_PAGE(HttpStatus.BAD_REQUEST.value(), "5","올바르지 않은 페이지입니다."),
    WRONG_AMOUNT(HttpStatus.BAD_REQUEST.value(), "6","금액을 확인해 주세요."),
    NON_EXIST_COIN(HttpStatus.BAD_REQUEST.value(), "7","구매하지 않은 코인입니다."),
    EXIST_NICKNAME(HttpStatus.OK.value(), "8","중복된 닉네임이 존재합니다."),
    WRONG_VALUE_NICKNAME(HttpStatus.BAD_REQUEST.value(), "9","닉네임은 특수문자를 제외한 4~9 자입니다."),
    FAIL_CONVERT_FILE(HttpStatus.BAD_REQUEST.value(), "10","파일 변환에 실패하였습니다."),
    NON_EXIST_RIVAL(HttpStatus.BAD_REQUEST.value(), "11","라이벌이 존재하지 않습니다."),
    NON_AVAILABLE_TOKEN(HttpStatus.FORBIDDEN.value(), "12","유효하지 않은 토큰입니다.")
    ;

    private final int httpStatus;
    private final String code;
    private final String message;

}
