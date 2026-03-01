package com.example.starter;

import com.example.starter.model.Activity;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ActivityService {

  private final ConcurrentHashMap<String, Activity> activities = new ConcurrentHashMap<>();
  private final Vertx vertx;

  public ActivityService(Vertx vertx) {
    this.vertx = vertx;
    // Add some sample activities
    addSampleActivities();
  }

  public Vertx getVertx() {
    return vertx;
  }

  private void addSampleActivities() {
    Activity activity1 = new Activity(
      UUID.randomUUID().toString(),
      "Team Meeting",
      "Weekly team sync",
      LocalDateTime.now().plusHours(2),
      true
    );
    Activity activity2 = new Activity(
      UUID.randomUUID().toString(),
      "Code Review",
      "Review PR #123",
      LocalDateTime.now().plusDays(1),
      false
    );
    activities.put(activity1.getId(), activity1);
    activities.put(activity2.getId(), activity2);
  }

  public Future<List<Activity>> getAllActivities() {
    Promise<List<Activity>> promise = Promise.promise();
    vertx.executeBlocking((Handler<Promise<List<Activity>>>) future -> {
      future.complete(new ArrayList<>(activities.values()));
    }).onSuccess(promise::complete).onFailure(promise::fail);
    return promise.future();
  }

  public Future<Activity> getActivityById(String id) {
    Promise<Activity> promise = Promise.promise();
    vertx.executeBlocking((Handler<Promise<Activity>>) future -> {
      future.complete(activities.get(id));
    }).onSuccess(activity -> {
      if (activity != null) {
        promise.complete(activity);
      } else {
        promise.fail("Activity not found");
      }
    }).onFailure(promise::fail);
    return promise.future();
  }

  public Future<Activity> createActivity(Activity activity) {
    Promise<Activity> promise = Promise.promise();
    vertx.executeBlocking((Handler<Promise<Activity>>) future -> {
      String id = UUID.randomUUID().toString();
      activity.setId(id);
      activity.setNotified(false);
      activities.put(id, activity);
      future.complete(activity);
    }).onSuccess(promise::complete).onFailure(promise::fail);
    return promise.future();
  }

  public Future<Activity> updateActivity(String id, Activity updatedActivity) {
    Promise<Activity> promise = Promise.promise();
    vertx.executeBlocking((Handler<Promise<Activity>>) future -> {
      Activity existing = activities.get(id);
      if (existing != null) {
        updatedActivity.setId(id);
        updatedActivity.setNotified(existing.isNotified());
        activities.put(id, updatedActivity);
        future.complete(updatedActivity);
      } else {
        future.complete(null);
      }
    }).onSuccess(activity -> {
      if (activity != null) {
        promise.complete(activity);
      } else {
        promise.fail("Activity not found");
      }
    }).onFailure(promise::fail);
    return promise.future();
  }

  public Future<Boolean> deleteActivity(String id) {
    Promise<Boolean> promise = Promise.promise();
    vertx.executeBlocking((Handler<Promise<Boolean>>) future -> {
      future.complete(activities.remove(id) != null);
    }).onSuccess(promise::complete).onFailure(promise::fail);
    return promise.future();
  }

  public Future<List<Activity>> getUpcomingActivitiesWithNotification() {
    Promise<List<Activity>> promise = Promise.promise();
    vertx.executeBlocking((Handler<Promise<List<Activity>>>) future -> {
      LocalDateTime now = LocalDateTime.now();
      List<Activity> result = activities.values().stream()
        .filter(a -> a.isNotificationEnabled() && !a.isNotified())
        .filter(a -> a.getDateTime() != null && a.getDateTime().isAfter(now))
        .filter(a -> a.getDateTime().isBefore(now.plusMinutes(15)))
        .collect(Collectors.toList());
      future.complete(result);
    }).onSuccess(promise::complete).onFailure(promise::fail);
    return promise.future();
  }

  public Future<Activity> markAsNotified(String id) {
    Promise<Activity> promise = Promise.promise();
    vertx.executeBlocking((Handler<Promise<Activity>>) future -> {
      Activity activity = activities.get(id);
      if (activity != null) {
        activity.setNotified(true);
        activities.put(id, activity);
      }
      future.complete(activity);
    }).onSuccess(promise::complete).onFailure(promise::fail);
    return promise.future();
  }
}

