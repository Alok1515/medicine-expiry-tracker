package com.medtracker.service;

import com.medtracker.model.Medicine;
import com.medtracker.model.Notification;
import com.medtracker.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpirySchedulerServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirySchedulerService expirySchedulerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(expirySchedulerService, "warningDaysConfig", "3,7,14");
    }

    @Test
    void shouldSendNotificationForExpiredMedicines() {
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        Medicine expiredMedicine = new Medicine();
        expiredMedicine.setId("med-1");
        expiredMedicine.setUserId("user-1");
        expiredMedicine.setName("Old Medicine");
        expiredMedicine.setExpiryDate(twoDaysAgo);

        when(medicineRepository.findByExpiryDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(expiredMedicine));
        when(medicineRepository.findByExpiryDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        expirySchedulerService.checkExpiringMedicines();

        ArgumentCaptor<Notification.NotificationType> typeCaptor = ArgumentCaptor.forClass(Notification.NotificationType.class);
        verify(notificationService, times(1)).createAndPushNotification(
                eq(expiredMedicine),
                typeCaptor.capture(),
                any(String.class));
        assertEquals(Notification.NotificationType.EXPIRED, typeCaptor.getValue());
    }

    @Test
    void shouldSendNotificationForExpiringSoonMedicines() {
        LocalDate today = LocalDate.now();
        LocalDate inSevenDays = today.plusDays(7);
        Medicine expiringMedicine = new Medicine();
        expiringMedicine.setId("med-2");
        expiringMedicine.setUserId("user-1");
        expiringMedicine.setName("Expiring Soon");
        expiringMedicine.setExpiryDate(inSevenDays);

        when(medicineRepository.findByExpiryDateBefore(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(medicineRepository.findByExpiryDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList(), List.of(expiringMedicine), Collections.emptyList());

        expirySchedulerService.checkExpiringMedicines();

        ArgumentCaptor<Notification.NotificationType> typeCaptor = ArgumentCaptor.forClass(Notification.NotificationType.class);
        verify(notificationService, times(1)).createAndPushNotification(
                eq(expiringMedicine),
                typeCaptor.capture(),
                any(String.class));
        assertEquals(Notification.NotificationType.EXPIRING_SOON, typeCaptor.getValue());
    }

    @Test
    void shouldNotSendNotificationWhenAllMedicinesAreSafe() {
        when(medicineRepository.findByExpiryDateBefore(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(medicineRepository.findByExpiryDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        expirySchedulerService.checkExpiringMedicines();

        verify(notificationService, never()).createAndPushNotification(
                any(Medicine.class),
                any(Notification.NotificationType.class),
                any(String.class));
    }
}
