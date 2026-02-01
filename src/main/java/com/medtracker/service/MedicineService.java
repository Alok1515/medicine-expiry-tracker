package com.medtracker.service;

import com.medtracker.dto.MedicineDTO;
import com.medtracker.model.Medicine;
import com.medtracker.repository.MedicineRepository;
import com.medtracker.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final NotificationRepository notificationRepository;

    public MedicineService(MedicineRepository medicineRepository,
                           NotificationRepository notificationRepository) {
        this.medicineRepository = medicineRepository;
        this.notificationRepository = notificationRepository;
    }

    public Medicine addMedicine(MedicineDTO dto, String userId) {
        Medicine medicine = new Medicine();
        medicine.setUserId(userId);
        medicine.setName(dto.getName());
        medicine.setManufacturer(dto.getManufacturer());
        medicine.setBatchNumber(dto.getBatchNumber());
        medicine.setCategory(dto.getCategory());
        medicine.setPurchaseDate(dto.getPurchaseDate());
        medicine.setExpiryDate(dto.getExpiryDate());
        medicine.setQuantity(dto.getQuantity());
        medicine.setDosage(dto.getDosage());
        medicine.setNotes(dto.getNotes());
        medicine.setImageUrl(dto.getImageUrl());
        return medicineRepository.save(medicine);
    }

    public List<Medicine> getAllMedicines(String userId) {
        return medicineRepository.findByUserIdOrderByExpiryDate(userId);
    }

    public Medicine getMedicine(String medicineId, String userId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        if (!userId.equals(medicine.getUserId())) {
            throw new RuntimeException("Medicine not found");
        }
        return medicine;
    }

    public Medicine updateMedicine(String medicineId, MedicineDTO dto, String userId) {
        Medicine medicine = getMedicine(medicineId, userId);
        medicine.setName(dto.getName());
        medicine.setManufacturer(dto.getManufacturer());
        medicine.setBatchNumber(dto.getBatchNumber());
        medicine.setCategory(dto.getCategory());
        medicine.setPurchaseDate(dto.getPurchaseDate());
        medicine.setExpiryDate(dto.getExpiryDate());
        medicine.setQuantity(dto.getQuantity());
        medicine.setDosage(dto.getDosage());
        medicine.setNotes(dto.getNotes());
        medicine.setImageUrl(dto.getImageUrl());
        medicine.setUpdatedAt(LocalDateTime.now());
        return medicineRepository.save(medicine);
    }

    public void deleteMedicine(String medicineId, String userId) {
        getMedicine(medicineId, userId);
        notificationRepository.deleteByMedicineId(medicineId);
        medicineRepository.deleteById(medicineId);
    }

    public List<Medicine> searchMedicines(String userId, String keyword) {
        return medicineRepository.searchByName(userId, keyword);
    }

    public Map<String, Long> getDashboardStats(String userId) {
        List<Medicine> all = medicineRepository.findByUserIdOrderByExpiryDate(userId);
        System.out.println("Total medicines for user " + userId + ": " + all.size());
        LocalDate today = LocalDate.now();
        LocalDate weekFromNow = today.plusDays(7);
        long total = all.size();
        long expired = all.stream()
                .filter(m -> m.getExpiryDate() != null && m.getExpiryDate().isBefore(today))
                .count();
        long expiringSoon = all.stream()
                .filter(m -> m.getExpiryDate() != null
                        && !m.getExpiryDate().isBefore(today)
                        && !m.getExpiryDate().isAfter(weekFromNow))
                .count();
        long safe = total - expired - expiringSoon;
        return Map.of(
                "total", total,
                "expired", expired,
                "expiringSoon", expiringSoon,
                "safe", safe
        );
    }
}
