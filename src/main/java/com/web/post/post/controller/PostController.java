package com.web.post.post.controller;

import com.web.post.global.dto.LoginUser;
import com.web.post.post.dto.*;
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

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@LoginUser User loginUser, @PathVariable Long postId, @RequestBody PostUpdateRequest request){
        return ResponseEntity.ok(postService.updatePost(loginUser.getId(), postId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDto> deletePost(@LoginUser User loginUser, @PathVariable Long postId){
        return ResponseEntity.ok(postService.delete(loginUser.getId(), postId));
    }


}
