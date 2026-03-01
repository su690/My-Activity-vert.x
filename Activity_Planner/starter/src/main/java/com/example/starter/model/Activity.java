package com.example.starter.model;

import java.time.LocalDateTime;

public class Activity {
    private String id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private boolean notificationEnabled;
    private boolean notified;

    public Activity() {
    }

    public Activity(String id, String title, String description, LocalDateTime dateTime, boolean notificationEnabled) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.notificationEnabled = notificationEnabled;
        this.notified = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
