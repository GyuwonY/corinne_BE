package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestdto {

    private Long userId;
    private String userEmail;
    private String nickname;
    private String password;

}
