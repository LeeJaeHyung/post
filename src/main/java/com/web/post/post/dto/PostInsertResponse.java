package com.web.post.post.dto;

import com.web.post.post.domain.Post;
import com.web.post.post.domain.PostStatus;
import lombok.Getter;


@Getter
public class PostInsertResponse {

    private Long id;
    private String title;
    private String content;
    private String author;
    private PostStatus status;

    public PostInsertResponse(Post post, String author) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = author;
        this.status = post.getStatus();
    }
}
