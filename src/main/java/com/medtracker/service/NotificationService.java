package com.medtracker.service;

import com.medtracker.dto.NotificationDTO;
import com.medtracker.model.Medicine;
import com.medtracker.model.Notification;
import com.medtracker.repository.NotificationRepository;
import com.medtracker.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               EmailService emailService,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    public void createAndPushNotification(Medicine medicine, Notification.NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setUserId(medicine.getUserId());
        notification.setMedicineId(medicine.getId());
        notification.setMedicineName(medicine.getName());
        notification.setType(type);
        notification.setMessage(message);
        Notification saved = notificationRepository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + medicine.getUserId(),
                NotificationDTO.from(saved)
        );

        if (type == Notification.NotificationType.EXPIRED || type == Notification.NotificationType.EXPIRING_SOON) {
            userRepository.findById(medicine.getUserId()).ifPresent(user -> {
                if (user.isEmailNotifications()) {
                    String expiryDateStr = medicine.getExpiryDate() != null
                            ? medicine.getExpiryDate().toString()
                            : "N/A";
                    emailService.sendExpiryAlert(
                            user.getEmail(),
                            medicine.getName(),
                            expiryDateStr,
                            type.name()
                    );
                }
            });
        }
    }

    public List<NotificationDTO> getNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationDTO::from)
                .toList();
    }

    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(String userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }
}
