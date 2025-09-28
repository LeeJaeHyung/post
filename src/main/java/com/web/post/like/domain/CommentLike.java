package com.web.post.like.domain;

import com.web.post.comment.domain.Comment;
import com.web.post.like.domain.id.CommentLikeId;
import com.web.post.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_likes",
        indexes = {
                @Index(name = "idx_likes_user_created", columnList = "user_id, created_at")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommentLike {

    @EmbeddedId
    private CommentLikeId id;

    @MapsId("commentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_commentlikes_comment"))
    private Comment comment;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_commentlikes_user"))
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
