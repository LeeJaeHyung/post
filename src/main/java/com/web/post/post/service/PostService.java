package com.web.post.post.service;

import com.web.post.post.domain.Post;
import com.web.post.post.domain.PostStatus;
import com.web.post.post.dto.PostInsertRequest;
import com.web.post.post.dto.PostInsertResponse;
import com.web.post.post.dto.PostListResponse;
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
}
