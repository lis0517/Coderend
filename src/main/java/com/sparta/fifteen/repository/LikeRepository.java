package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.ContentTypeEnum;
import com.sparta.fifteen.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {

    Optional<Like> findByUserIdAndContentIdAndContentType(Long userId, Long contentId, ContentTypeEnum contentType);
}
