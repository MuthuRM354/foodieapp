package com.foodieapp.favorite.repository;

import com.foodieapp.favorite.model.FavoriteCuisine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteCuisineRepository extends JpaRepository<FavoriteCuisine, Long> {
    List<FavoriteCuisine> findByUserId(String userId);
    Optional<FavoriteCuisine> findByUserIdAndCuisineId(String userId, String cuisineId);
    void deleteByUserIdAndCuisineId(String userId, String cuisineId);
}
