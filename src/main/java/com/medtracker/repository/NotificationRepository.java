package com.medtracker.repository;

import com.medtracker.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Notification> findByUserIdAndReadFalse(String userId);

    long countByUserIdAndReadFalse(String userId);

    void deleteByMedicineId(String medicineId);

    boolean existsByMedicineIdAndType(String medicineId, Notification.NotificationType type);
}
