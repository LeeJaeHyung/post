package com.web.post.comment.repository;

import com.web.post.comment.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query(value = """
WITH RECURSIVE r AS (
  -- 1) 루트 댓글
  SELECT
      c.comment_id,
      c.post_id,
      c.parent_id,
      c.author_id,
      c.content,
      COALESCE(c.position, 0) AS position,
      c.created_at,
      c.created_at AS root_created_at,
      CAST(LPAD(COALESCE(c.position, 0), 6, '0') AS CHAR(1024)) AS sort_path,
      COALESCE(c.like_count, 0) AS like_count
  FROM comments c
  WHERE c.post_id = :postId
    AND c.parent_id IS NULL

  UNION ALL

  -- 2) 자식 댓글
  SELECT
      ch.comment_id,
      ch.post_id,
      ch.parent_id,
      ch.author_id,
      ch.content,
      COALESCE(ch.position, 0) AS position,
      ch.created_at,
      r.root_created_at,
      CAST(CONCAT(r.sort_path, '.', LPAD(COALESCE(ch.position, 0), 6, '0')) AS CHAR(1024)) AS sort_path,
      COALESCE(ch.like_count, 0) AS like_count
  FROM comments ch
  JOIN r ON ch.parent_id = r.comment_id
)
SELECT
    comment_id, post_id, parent_id, author_id, content, position, created_at, like_count
FROM r
ORDER BY
    root_created_at ASC,  -- 최상위 댓글 묶음 순서
    sort_path ASC,        -- 대댓글 경로 순서
    created_at ASC        -- 타이브레이커
""", nativeQuery = true)
    List<Comment> findThreadedByPostId(@Param("postId") Long postId);


    @Query(value = """
        SELECT IFNULL(MAX(c.position), 0) + 1
        FROM comments c
        WHERE c.parent_id = :parentId
    """, nativeQuery = true)
    int findPosition(@Param("parentId") Long parentId);


    @Modifying
    @Transactional
    @Query("update Comment c set c.likeCount = c.likeCount + 1 where c.id = :commentId")
    int incLikeCount(@Param("commentId") Long commentId);

    @Modifying @Transactional
    @Query("update Comment c set c.likeCount = CASE WHEN c.likeCount > 0 THEN c.likeCount - 1 ELSE 0 END " +
            "where c.id = :commentId")
    int decLikeCount(@Param("commentId") Long commentId);

    boolean existsByPost_IdAndId(Long postId, Long commentId);
}
