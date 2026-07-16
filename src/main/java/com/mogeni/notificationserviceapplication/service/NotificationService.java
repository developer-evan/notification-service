package com.mogeni.notificationserviceapplication.service;


import com.mogeni.notificationserviceapplication.dto.NotificationRequest;
import com.mogeni.notificationserviceapplication.dto.NotificationResponse;

public interface NotificationService {

    NotificationResponse createNotification(NotificationRequest request);

    NotificationResponse getNotificationById(String id);
}
