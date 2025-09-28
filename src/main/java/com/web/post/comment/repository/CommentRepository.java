package com.web.post.comment.repository;

import com.web.post.comment.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostId(Long post_id);


    @Query(value = """
        SELECT IFNULL(MAX(c.position), 0) + 1
        FROM comments c
        WHERE c.parent_id = :parentId
    """, nativeQuery = true)
    int findPosition(@Param("parentId") Long parentId);
}
