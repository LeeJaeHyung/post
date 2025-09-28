package com.web.post.like.repository;

import com.web.post.like.domain.PostLike;
import com.web.post.like.domain.id.PostLikeId;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from PostLike pl where pl.post.id = :postId and pl.user.id = :userId")
    int deleteById(@Param("postId") Long postId, @Param("userId") Long userId);

    long countByPost_Id(Long postId);
}