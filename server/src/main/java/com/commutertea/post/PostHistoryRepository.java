package com.commutertea.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHistoryRepository extends JpaRepository<PostHistory, Long> {

    Optional<PostHistory> findByPostIdAndUserId(Long postId, Long userId);

    List<PostHistory> findByUserIdOrderByViewedAtDesc(Long userId);
}
