package com.medtracker.dto;

import com.medtracker.model.Notification;

import java.time.LocalDateTime;

public class NotificationDTO {

    private String id;
    private String medicineId;
    private String medicineName;
    private Notification.NotificationType type;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public static NotificationDTO from(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setMedicineId(n.getMedicineId());
        dto.setMedicineName(n.getMedicineName());
        dto.setType(n.getType());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public Notification.NotificationType getType() {
        return type;
    }

    public void setType(Notification.NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
