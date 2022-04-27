package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestdto {

    private String userEmail;
    private String password;
    private String nickname;
}
