package com.web.post.post.controller;

import ch.qos.logback.core.model.Model;
import com.web.post.global.dto.LoginUser;
import com.web.post.post.dto.PostInsertRequest;
import com.web.post.post.dto.PostInsertResponse;
import com.web.post.post.dto.PostListResponse;
import com.web.post.post.service.PostService;
import com.web.post.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @ResponseBody
    @GetMapping("")
    public ResponseEntity<PostListResponse> getPosts(){
        return ResponseEntity.ok(postService.getPosts());
    }

    @PostMapping("")
    @ResponseBody
    public ResponseEntity<PostInsertResponse> insertPost(@LoginUser User loginUser, @RequestBody PostInsertRequest request){
        return ResponseEntity.ok(postService.insertPost(loginUser,request));
    }


}
