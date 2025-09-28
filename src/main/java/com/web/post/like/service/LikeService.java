package com.web.post.like.service;

import com.web.post.comment.domain.Comment;
import com.web.post.comment.repository.CommentRepository;
import com.web.post.like.domain.CommentLike;
import com.web.post.like.domain.PostLike;
import com.web.post.like.domain.id.CommentLikeId;
import com.web.post.like.domain.id.PostLikeId;
import com.web.post.like.dto.CommentLikeResult;
import com.web.post.like.dto.PostLikeResult;
import com.web.post.like.repository.CommentLikeRepository;
import com.web.post.like.repository.PostLikeRepository;
import com.web.post.post.domain.Post;
import com.web.post.post.repository.PostRepository;
import com.web.post.user.domain.User;
import com.web.post.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostLikeResult likePost(Long postId, Long userId) {
        // (선택) 사용자/게시글 유효성 체크: likeComment와 결을 맞추려면 넣는 게 깔끔
        userRepository.findById(userId).orElseThrow();
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        // 1) 먼저 삭제 시도 (이미 좋아요 상태였다면 1 이상)
        long deleted = postLikeRepository.deleteById(postId, userId);
        if (deleted > 0) {
            long count = postLikeRepository.countByPost_Id(postId);
             postRepository.decLikeCount(postId); // 카운터 컬럼을 쓰고 싶으면 주석 해제
            return new PostLikeResult(false, count); // 좋아요 취소됨
        }

        // 2) 삭제가 안 됐다면 새로 좋아요
        PostLike like = PostLike.builder()
                .id(new PostLikeId(postId, userId))
                .post(Post.builder().id(postId).build())
                .user(User.builder().id(userId).build())
                .build();
        try {
            postLikeRepository.save(like);
             postRepository.incLikeCount(postId); // 카운터 컬럼 사용하는 경우 주석 해제
        } catch (DataIntegrityViolationException e) {
            // 동시성으로 이미 들어간 경우: 최종 상태는 '좋아요됨' 이므로 무시
        }

        long count = postLikeRepository.countByPost_Id(postId);
        return new PostLikeResult(true, count); // 좋아요 추가됨
    }

    @Transactional
    public CommentLikeResult likeComment(Long postId, Long commentId, Long userId) {
        // 사용자/댓글 유효성 체크 (likePost와 동일한 결)
        userRepository.findById(userId).orElseThrow();
        if (!commentRepository.existsByPost_IdAndId(postId, commentId)) {
            throw new IllegalArgumentException("게시글에 존재 하지 않는 댓글입니다.");
        }

        // 1) 먼저 삭제 시도 (이미 좋아요 상태였다면 1 이상)
        long deleted = commentLikeRepository.deleteById(commentId, userId);
        if (deleted > 0) {
            int count = commentLikeRepository.countByComment_Id(commentId);
            commentRepository.decLikeCount(commentId); // likePost와 동일하게 '카운트 조회 후 감소'
            return new CommentLikeResult(false, count); // 좋아요 취소됨
        }

        // 2) 삭제가 안 됐다면 새로 좋아요
        CommentLike like = CommentLike.builder()
                .id(new CommentLikeId(commentId, userId))
                .comment(Comment.builder().id(commentId).build())
                .user(User.builder().id(userId).build())
                .build();

        try {
            commentLikeRepository.save(like);
            commentRepository.incLikeCount(commentId); // likePost와 동일하게 '저장 후 증가'
        } catch (DataIntegrityViolationException e) {
            // 동시성 레이스로 이미 들어간 경우: 최종 상태는 '좋아요됨'이므로 무시
        }

        int count = commentLikeRepository.countByComment_Id(commentId);
        return new CommentLikeResult(true, count); // 좋아요 추가됨
    }


}
