package com.web.post.post.dto;

import com.web.post.post.domain.Post;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostListResponse {
    private List<PostDto> postList;
    public PostListResponse(List<Post> postList) {
        this.postList = new ArrayList<>();
        for (Post post : postList) {
            this.postList.add(new PostDto(post));
        }
    }
}
