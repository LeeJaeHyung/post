package com.web.post.comment.service;

import com.web.post.comment.domain.Comment;
import com.web.post.comment.dto.CommentDto;
import com.web.post.comment.dto.CommentInsertRequest;
import com.web.post.comment.dto.CommentUpdateRequest;
import com.web.post.comment.repository.CommentRepository;
import com.web.post.post.domain.Post;
import com.web.post.post.repository.PostRepository;
import com.web.post.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long postId) {
        List<Comment> CommentList = commentRepository.findThreadedByPostId(postId);
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : CommentList) {
            CommentDto dto = new CommentDto(comment);
            commentDtoList.add(dto);
        }
        return commentDtoList;
    }

    @Transactional
    public CommentDto insertComment(Long postId, User loginUser, CommentInsertRequest request) {
        Comment comment = new Comment();
        comment.setAuthor(loginUser);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물 입니다."));
        comment.setPost(post);
        comment.setContent(request.getComment());
        if (request.getParentId()!=null){
            Comment parentComment = commentRepository.findById(request.getParentId()).orElseThrow(()->new IllegalArgumentException("대상 댓글이 없습니다."));
            comment.setParent(parentComment);
            comment.setPosition(commentRepository.findPosition(request.getParentId()));
        }
        return new CommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, Long loginUserId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findByIdAndPostIdAndAuthorId(commentId,postId,loginUserId).orElseThrow(()-> new IllegalArgumentException("댓글에 접근 권한이 없습니다."));
        comment.setContent(request.getComment());
        return new CommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto deleteComment(Long postId, Long commentId, Long loginUserId) {
        Comment comment = commentRepository.findByIdAndPostIdAndAuthorId(commentId,postId,loginUserId).orElseThrow(()-> new IllegalArgumentException("댓글에 접근 권한이 없습니다."));
        comment.setCreatedAt(null);
        return new CommentDto(commentRepository.save(comment));
    }
}
