package com.web.post.user.dto.response;

import com.web.post.global.dto.LoginUserDto;
import lombok.Getter;

@Getter
public class UserLoginResult {
    private String token;
    private LoginUserDto loginUserDto;
    public UserLoginResult(String token, LoginUserDto loginUserDto) {
        this.token = token;
        this.loginUserDto = loginUserDto;
    }
}
