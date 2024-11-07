package com.foodieapp.user.repository;

import com.foodieapp.user.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByFileId(String fileId);
    void deleteByFileId(String fileId);
    List<Image> findByUserId(Long userId);
    List<Image> findByUserIdAndCategory(Long userId, String category);
}
