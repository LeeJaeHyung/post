package com.web.post.post.service;

import com.web.post.comment.repository.CommentRepository;
import com.web.post.post.domain.Post;
import com.web.post.post.domain.PostStatus;
import com.web.post.post.dto.*;
import com.web.post.post.repository.PostRepository;
import com.web.post.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public PostInsertResponse insertPost(User loginUser, PostInsertRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(loginUser);
        post.setStatus(PostStatus.PUBLIC);
        post.setCommentCount(0);
        post.setLikeCount(0);
        post.setViewCount(0);
        return new PostInsertResponse(postRepository.save(post),loginUser.getUsername());
    }

    @Transactional(readOnly = true)
    public PostListResponse getPosts() {
        List<Post> posts = postRepository.findAll();
        return new PostListResponse(posts);
    }

    @Transactional
    public PostDto updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findByIdAndAuthorId(postId,userId).orElseThrow(()-> new IllegalArgumentException("접근 가능한 게시물이 아닙니다."));
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return new PostDto(postRepository.save(post));
    }

    @Transactional
    public PostDto delete(Long id, Long postId) {
        Post post = postRepository.findByIdAndAuthorId(postId, id).orElseThrow(()-> new IllegalArgumentException("접근 가능한 게시물이 아닙니다."));
        post.setStatus(PostStatus.DELETED);
        post.setCreatedAt(null);
        commentRepository.softDeleteByPostId(postId);
        return new PostDto(postRepository.save(post));
    }
}
