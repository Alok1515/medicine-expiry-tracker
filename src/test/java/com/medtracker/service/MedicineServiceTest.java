package com.medtracker.service;

import com.medtracker.dto.MedicineDTO;
import com.medtracker.model.Medicine;
import com.medtracker.repository.MedicineRepository;
import com.medtracker.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private MedicineService medicineService;

    @Test
    void shouldAddMedicine() {
        MedicineDTO dto = new MedicineDTO();
        dto.setName("Paracetamol");
        dto.setExpiryDate(LocalDate.now().plusMonths(6));
        dto.setQuantity(10);
        dto.setCategory("Pain Relief");

        String userId = "user-1";
        Medicine saved = new Medicine();
        saved.setId("med-123");
        saved.setUserId(userId);
        saved.setName(dto.getName());
        saved.setExpiryDate(dto.getExpiryDate());
        saved.setQuantity(dto.getQuantity());
        saved.setCategory(dto.getCategory());

        when(medicineRepository.save(any(Medicine.class))).thenReturn(saved);

        Medicine result = medicineService.addMedicine(dto, userId);

        assertEquals("med-123", result.getId());
        assertEquals("Paracetamol", result.getName());
        assertEquals(userId, result.getUserId());
        verify(medicineRepository).save(any(Medicine.class));
    }

    @Test
    void shouldThrowWhenMedicineNotFound() {
        String medicineId = "non-existent";
        String userId = "user-1";
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                medicineService.getMedicine(medicineId, userId));
    }

    @Test
    void shouldPreventAccessToOtherUserMedicine() {
        String medicineId = "med-1";
        Medicine medicine = new Medicine();
        medicine.setId(medicineId);
        medicine.setUserId("user1");
        medicine.setName("Aspirin");

        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(medicine));

        assertThrows(RuntimeException.class, () ->
                medicineService.getMedicine(medicineId, "user2"));
    }

    @Test
    void shouldReturnCorrectDashboardStats() {
        String userId = "user-1";
        LocalDate today = LocalDate.now();

        Medicine expired1 = new Medicine();
        expired1.setId("1");
        expired1.setUserId(userId);
        expired1.setExpiryDate(today.minusDays(5));

        Medicine expired2 = new Medicine();
        expired2.setId("2");
        expired2.setUserId(userId);
        expired2.setExpiryDate(today.minusDays(1));

        Medicine expiringSoon = new Medicine();
        expiringSoon.setId("3");
        expiringSoon.setUserId(userId);
        expiringSoon.setExpiryDate(today.plusDays(3));

        Medicine safe1 = new Medicine();
        safe1.setId("4");
        safe1.setUserId(userId);
        safe1.setExpiryDate(today.plusMonths(2));

        Medicine safe2 = new Medicine();
        safe2.setId("5");
        safe2.setUserId(userId);
        safe2.setExpiryDate(today.plusMonths(6));

        List<Medicine> medicines = List.of(expired1, expired2, expiringSoon, safe1, safe2);
        when(medicineRepository.findByUserIdOrderByExpiryDate(userId)).thenReturn(medicines);

        Map<String, Long> stats = medicineService.getDashboardStats(userId);

        assertEquals(5L, stats.get("total"));
        assertEquals(2L, stats.get("expired"));
        assertEquals(1L, stats.get("expiringSoon"));
        assertEquals(2L, stats.get("safe"));
    }
}
