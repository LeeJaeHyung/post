package com.web.post.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInsertRequest {
    private String username;
    private String email;
    private String password;
}
