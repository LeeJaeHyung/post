package com.web.post.comment.dto;

import com.web.post.comment.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    Long id;
    Long post_id;
    Long parent_id;
    Integer position;
    String author;
    String content;
    LocalDateTime createdAt;
    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.post_id = comment.getPost().getId();
        if (comment.getParent() != null) {
            this.parent_id = comment.getParent().getId();
        }
        this.position = comment.getPosition();
        this.author = comment.getAuthor().getUsername();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}
