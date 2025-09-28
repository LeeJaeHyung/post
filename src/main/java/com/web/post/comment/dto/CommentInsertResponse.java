package com.web.post.comment.dto;

import lombok.Getter;

@Getter
public class CommentInsertResponse {
    CommentDto commentDto;
    public CommentInsertResponse(CommentDto commentDto) {
        this.commentDto = commentDto;
    }
}
