package com.web.post.user.service;

import com.web.post.global.dto.LoginUserDto;
import com.web.post.global.service.RedisLoginTokenService;
import com.web.post.user.domain.User;
import com.web.post.user.dto.request.UserInsertRequest;
import com.web.post.user.dto.request.UserLoginRequest;
import com.web.post.user.dto.response.UserLoginResult;
import com.web.post.user.dto.response.UserSignupResponse;
import com.web.post.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.web.post.global.util.PasswordEncoder.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RedisLoginTokenService redisLoginTokenService;

    public User getUser() {
        User user = userRepository.findById(1L).get();
        return user;
    }

    public UserSignupResponse insertUser(UserInsertRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        String salt = generateSalt();
        String hashedPassword = encode(request.getPassword(), salt);
        user.setSalt(salt);
        user.setPasswordHash(hashedPassword);
        User insertUser = userRepository.save(user);
        return new UserSignupResponse(insertUser.getUsername(), insertUser.getEmail());
    }

    public UserLoginResult login(UserLoginRequest request) {
        User targetUser = userRepository.findByUsername(request.getUsername()).orElseThrow(() ->new IllegalArgumentException("존재하지 않는 유저 입니다."));
        String hashedPassword = targetUser.getPasswordHash();
        String salt = targetUser.getSalt();
        if (comparePassword(request.getPassword(), salt, hashedPassword)) {
            //토큰 생성
            String token = UUID.randomUUID().toString();

            // Redis에 로그인 정보 저장
            LoginUserDto dto = new LoginUserDto(
                    targetUser.getId(),
                    targetUser.getUsername(),
                    targetUser.getEmail()
            );

            redisLoginTokenService.save(token, dto);// Duration 설정 포함됨
            return new UserLoginResult(token, dto);
        }else{
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
    }
}
