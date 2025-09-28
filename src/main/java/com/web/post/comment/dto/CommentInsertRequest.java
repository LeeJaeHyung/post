package com.web.post.comment.dto;

import lombok.Getter;

@Getter
public class CommentInsertRequest {
    private String comment;
    private Long parentId;
}
