package com.corinne.corinne_be.repository;

import com.corinne.corinne_be.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserIdAndTiker(Long userId, String tiker);
    Optional<Bookmark> findByUserIdAndTiker(Long userId, String tiker);

}
