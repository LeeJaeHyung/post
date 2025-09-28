package com.web.post.like.domain;

import com.web.post.like.domain.id.PostLikeId;
import com.web.post.post.domain.Post;
import com.web.post.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes",
        indexes = {
                @Index(name = "idx_likes_user_created", columnList = "user_id, created_at")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostLike {

    @EmbeddedId
    private PostLikeId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_postlikes_post"))
    private Post post;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_postlikes_user"))
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}