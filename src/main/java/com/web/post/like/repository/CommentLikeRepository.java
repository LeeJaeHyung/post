package com.web.post.like.repository;

import com.web.post.like.domain.CommentLike;
import com.web.post.like.domain.id.CommentLikeId;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from CommentLike cl " +
            "where cl.id.commentId = :commentId and cl.id.userId = :userId")
    int deleteById(@Param("commentId") Long commentId,
                   @Param("userId") Long userId);
    int countByComment_Id(Long commentId);
}
