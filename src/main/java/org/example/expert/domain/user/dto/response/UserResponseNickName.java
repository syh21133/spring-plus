package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponseNickName {


    private final String nickName;

    public UserResponseNickName(String nickName) {
        this.nickName = nickName;
    }

}
