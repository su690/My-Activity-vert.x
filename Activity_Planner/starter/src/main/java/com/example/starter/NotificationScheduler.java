package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class NotificationScheduler extends AbstractVerticle {

  private static final long CHECK_INTERVAL = 60000; // Check every minute
  private ActivityService activityService;
  private WebClient webClient;
  private long timerId;

  @Override
  public void start(Promise<Void> startPromise) {
    activityService = new ActivityService(vertx);
    webClient = WebClient.create(vertx, new WebClientOptions());
    
    // Start the periodic check
    timerId = vertx.setPeriodic(CHECK_INTERVAL, id -> checkUpcomingActivities());
    
    System.out.println("NotificationScheduler started");
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    if (timerId > 0) {
      vertx.cancelTimer(timerId);
    }
    if (webClient != null) {
      webClient.close();
    }
    System.out.println("NotificationScheduler stopped");
    stopPromise.complete();
  }

  private void checkUpcomingActivities() {
    activityService.getUpcomingActivitiesWithNotification()
      .onSuccess(activities -> {
        for (var activity : activities) {
          sendNotification(activity.getTitle(), activity.getDescription());
          activityService.markAsNotified(activity.getId());
        }
      })
      .onFailure(err -> {
        System.err.println("Error checking upcoming activities: " + err.getMessage());
      });
  }

  private void sendNotification(String title, String description) {
    // For demonstration, we'll log to console
    // In a real application, this could send email, push notification, etc.
    System.out.println("=====================================");
    System.out.println("NOTIFICATION: Upcoming Activity!");
    System.out.println("Title: " + title);
    System.out.println("Description: " + description);
    System.out.println("Time: Coming up in less than 15 minutes!");
    System.out.println("=====================================");
  }

  public ActivityService getActivityService() {
    return activityService;
  }
}

