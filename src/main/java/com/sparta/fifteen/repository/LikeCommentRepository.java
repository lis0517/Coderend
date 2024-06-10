package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {

    Optional<LikeComment> findByUserIdAndCommentId(long userId, long commentId); // IdAndCommentId(long userId, long commentId);
}
