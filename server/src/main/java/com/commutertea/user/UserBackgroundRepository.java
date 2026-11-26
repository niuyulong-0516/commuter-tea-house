package com.commutertea.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBackgroundRepository extends JpaRepository<UserBackground, Long> {

    List<UserBackground> findByUserIdOrderByCreatedAtDesc(Long userId);
}
