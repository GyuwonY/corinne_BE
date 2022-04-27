package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByKakaoId(Long kakaoId);

}
