package com.web.post.post.dto;

import com.web.post.post.domain.Post;
import com.web.post.post.domain.PostStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDto {
    private Long id;
    private String author;
    private String title;
    private String content;
    private PostStatus status;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostDto(Post post){
        this.id = post.getId();
        this.author = post.getAuthor().getUsername();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.status = post.getStatus();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.viewCount = post.getViewCount();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
