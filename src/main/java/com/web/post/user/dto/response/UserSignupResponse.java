package com.web.post.user.dto.response;

import lombok.Getter;

@Getter
public class UserSignupResponse {

    private String username;
    private String email;

    public UserSignupResponse(String username, String email){
        this.username = username;
        this.email = email;
    }
}
