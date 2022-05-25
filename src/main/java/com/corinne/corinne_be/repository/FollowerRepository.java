package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.Follower;
import com.corinne.corinne_be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
    boolean existsByUser_UserIdAndFollower_UserId(Long userId, Long followerId);
    List<Follower> findAllByUser(User user);
    Long countAllByUser(User user);
    Long countAllByFollower(User follower);
    void deleteByUserAndFollower(User user, User follower);
    boolean existsByUserAndFollower(User user, User follower);
}
