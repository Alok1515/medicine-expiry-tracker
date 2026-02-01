package com.medtracker.controller;

import com.medtracker.dto.NotificationDTO;
import com.medtracker.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private String getUserId(Authentication auth) {
        return (String) auth.getCredentials();
    }

    @GetMapping
    public List<NotificationDTO> getNotifications(Authentication auth) {
        return notificationService.getNotifications(getUserId(auth));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication auth) {
        notificationService.markAllAsRead(getUserId(auth));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(Authentication auth) {
        long count = notificationService.getUnreadCount(getUserId(auth));
        return Map.of("unreadCount", count);
    }
}
