package com.medtracker.service;

import com.medtracker.model.Medicine;
import com.medtracker.model.Notification;
import com.medtracker.repository.MedicineRepository;
import com.medtracker.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class ExpirySchedulerService {

    private static final Logger log = LoggerFactory.getLogger(ExpirySchedulerService.class);

    private final MedicineRepository medicineRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Value("${app.scheduler.expiry-warning-days}")
    private String warningDaysConfig;

    public ExpirySchedulerService(MedicineRepository medicineRepository,
                                 NotificationService notificationService,
                                 NotificationRepository notificationRepository) {
        this.medicineRepository = medicineRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.check-interval-ms}")
    public void checkExpiringMedicines() {
        List<Integer> warningDays = Arrays.stream(warningDaysConfig.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        log.info("Starting expiry check for date: {}", today);

        // 1. Process Expired Medicines (Today or earlier)
        List<Medicine> expired = medicineRepository.findByExpiryDateLessThanEqual(today);
        int expiredCount = 0;
        for (Medicine medicine : expired) {
            if (!notificationRepository.existsByMedicineIdAndType(medicine.getId(), Notification.NotificationType.EXPIRED)) {
                String message = String.format("%s expired on %s. Please dispose of it safely.",
                        medicine.getName(),
                        medicine.getExpiryDate());
                notificationService.createAndPushNotification(medicine, Notification.NotificationType.EXPIRED, message);
                expiredCount++;
            }
        }

        // 2. Process Expiring Soon
        int soonCount = 0;
        for (Integer days : warningDays) {
            LocalDate targetDate = today.plusDays(days);
            // Catch medicines expiring on OR before the target date that haven't been notified yet
            // and are not yet expired
            List<Medicine> expiringSoon = medicineRepository.findByExpiryDateBetween(today.plusDays(1), targetDate);
            
            for (Medicine medicine : expiringSoon) {
                if (!notificationRepository.existsByMedicineIdAndType(medicine.getId(), Notification.NotificationType.EXPIRING_SOON)) {
                    long daysUntil = today.until(medicine.getExpiryDate()).getDays();
                    String message = String.format("%s will expire in %d days (on %s). Please check and replace if needed.",
                            medicine.getName(),
                            daysUntil,
                            medicine.getExpiryDate());
                    notificationService.createAndPushNotification(medicine, Notification.NotificationType.EXPIRING_SOON, message);
                    soonCount++;
                }
            }
        }

        log.info("Expiry check completed. Sent {} EXPIRED and {} EXPIRING_SOON notifications.", expiredCount, soonCount);
    }
}
