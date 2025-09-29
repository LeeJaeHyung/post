package com.web.post.comment.controller;

import com.web.post.comment.dto.CommentDto;
import com.web.post.comment.dto.CommentInsertRequest;
import com.web.post.comment.dto.CommentUpdateRequest;
import com.web.post.comment.service.CommentService;
import com.web.post.global.dto.LoginUser;
import com.web.post.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}/comments")
    @ResponseBody
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable("postId") Long postId) {
        List<CommentDto> commentDtoList = commentService.getComments(postId);
        return ResponseEntity.ok(commentDtoList);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto> insertComment(@PathVariable("postId") Long postId, @LoginUser User loginUser, @RequestBody CommentInsertRequest request) {
        CommentDto dto = commentService.insertComment(postId, loginUser, request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @LoginUser User loginUser, @RequestBody CommentUpdateRequest request){
        return ResponseEntity.ok(commentService.updateComment(postId, commentId, loginUser.getId(),request));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @LoginUser User loginUser){
        return ResponseEntity.ok(commentService.deleteComment(postId, commentId, loginUser.getId()));
    }



}
