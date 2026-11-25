package com.commutertea.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostDraftRepository extends JpaRepository<PostDraft, Long> {

    List<PostDraft> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
