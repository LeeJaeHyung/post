package com.web.post.like.controller;

import com.web.post.global.dto.LoginUser;
import com.web.post.like.dto.CommentLikeResult;
import com.web.post.like.dto.PostLikeResult;
import com.web.post.like.service.LikeService;
import com.web.post.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<PostLikeResult> likePost(@LoginUser User loginUser, @PathVariable Long postId) {
        return ResponseEntity.ok(likeService.likePost(postId, loginUser.getId()));
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/likes")
    public ResponseEntity<CommentLikeResult> likeComment(@PathVariable Long postId, @PathVariable Long commentId, @LoginUser User loginUser) {
        return ResponseEntity.ok(likeService.likeComment(postId, commentId, loginUser.getId()));
    }



}
