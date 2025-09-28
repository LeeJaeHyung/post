package com.web.post.user.controller;


import com.web.post.user.domain.User;
import com.web.post.user.dto.request.UserInsertRequest;
import com.web.post.user.dto.request.UserLoginRequest;
import com.web.post.user.dto.response.UserLoginResult;
import com.web.post.user.dto.response.UserSignupResponse;
import com.web.post.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public User getUser(){
        User user = userService.getUser();
       return  user;
    }

    @PostMapping("")
    public ResponseEntity<UserSignupResponse> insertUser(@RequestBody UserInsertRequest request){
        return ResponseEntity.ok(userService.insertUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResult> login(@ModelAttribute UserLoginRequest request, HttpServletResponse response){
        UserLoginResult result = userService.login(request);
        Cookie cookie = new Cookie("access_token", result.getToken());
        cookie.setHttpOnly(true); // JavaScript로 접근 불가능 (XSS 방어)
        cookie.setPath("/");      // 모든 경로에서 전송
        cookie.setMaxAge(60 * 30); // 30분 유지
        response.addCookie(cookie); // 응답에 쿠키 추가
        return ResponseEntity.ok(result);
    }

}
