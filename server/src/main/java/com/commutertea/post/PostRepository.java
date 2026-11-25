package com.commutertea.post;

import com.commutertea.common.TransportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategory(PostCategory category, Pageable pageable);

    Page<Post> findByTransportType(TransportType transportType, Pageable pageable);

    Page<Post> findByCategoryAndTransportType(PostCategory category, TransportType transportType, Pageable pageable);

    Page<Post> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

    List<Post> findByGuestIdOrderByCreatedAtDesc(String guestId);
}
