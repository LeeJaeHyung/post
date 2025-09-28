package com.web.post.post.repository;

import com.web.post.post.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Modifying
    @Transactional
    @Query("update Post p set p.likeCount = p.likeCount + 1 where p.id = :postId")
    int incLikeCount(@Param("postId") Long postId);

    @Modifying @Transactional
    @Query("update Post p set p.likeCount = CASE WHEN p.likeCount > 0 THEN p.likeCount - 1 ELSE 0 END " +
            "where p.id = :postId")
    int decLikeCount(@Param("postId") Long postId);
}
