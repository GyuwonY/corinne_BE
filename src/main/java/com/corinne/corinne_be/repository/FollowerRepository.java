package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
    boolean existsByUser_UserIdAndFollower_UserId(Long userId, Long followerId);
}
