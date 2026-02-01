package com.medtracker.repository;

import com.medtracker.model.Medicine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MedicineRepository extends MongoRepository<Medicine, String> {

    List<Medicine> findByUserIdOrderByExpiryDate(String userId);

    List<Medicine> findByExpiryDateLessThanEqual(LocalDate date);

    List<Medicine> findByExpiryDateBefore(LocalDate date);

    List<Medicine> findByExpiryDateBetween(LocalDate start, LocalDate end);

    long countByUserId(String userId);

    @Query("{ 'userId': ?0, 'name': { $regex: ?1, $options: 'i' } }")
    List<Medicine> searchByName(String userId, String keyword);
}
