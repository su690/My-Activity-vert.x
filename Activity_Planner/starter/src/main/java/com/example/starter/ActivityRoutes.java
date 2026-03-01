package com.example.starter;

import com.example.starter.model.Activity;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityRoutes {

  private final ActivityService activityService;
  private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public ActivityRoutes(ActivityService activityService) {
    this.activityService = activityService;
  }

  public Router createRouter() {
    Router router = Router.router(activityService.getVertx());
    
    // Enable body parsing
    router.route().handler(BodyHandler.create());

    // API routes
    router.get("/activities").handler(this::getAllActivities);
    router.get("/activities/:id").handler(this::getActivityById);
    router.post("/activities").handler(this::createActivity);
    router.put("/activities/:id").handler(this::updateActivity);
    router.delete("/activities/:id").handler(this::deleteActivity);

    return router;
  }

  private void getAllActivities(RoutingContext rc) {
    activityService.getAllActivities()
      .onSuccess(activities -> {
        String json = convertActivitiesToJson(activities);
        rc.response()
          .putHeader("content-type", "application/json")
          .end(json);
      })
      .onFailure(err -> {
        rc.response()
          .setStatusCode(500)
          .end("Error: " + err.getMessage());
      });
  }

  private void getActivityById(RoutingContext rc) {
    String id = rc.pathParam("id");
    activityService.getActivityById(id)
      .onSuccess(activity -> {
        String json = convertActivityToJson(activity);
        rc.response()
          .putHeader("content-type", "application/json")
          .end(json);
      })
      .onFailure(err -> {
        rc.response()
          .setStatusCode(404)
          .end("Error: " + err.getMessage());
      });
  }

  private void createActivity(RoutingContext rc) {
    try {
      io.vertx.core.json.JsonObject body = rc.getBodyAsJson();
      Activity activity = new Activity();
      activity.setTitle(body.getString("title"));
      activity.setDescription(body.getString("description"));
      
      String dateTimeStr = body.getString("dateTime");
      if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
        activity.setDateTime(LocalDateTime.parse(dateTimeStr, ISO_FORMATTER));
      }
      
      activity.setNotificationEnabled(body.getBoolean("notificationEnabled", false));

      activityService.createActivity(activity)
        .onSuccess(created -> {
          String json = convertActivityToJson(created);
          rc.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json")
            .end(json);
        })
        .onFailure(err -> {
          rc.response()
            .setStatusCode(500)
            .end("Error: " + err.getMessage());
        });
    } catch (Exception e) {
      rc.response()
        .setStatusCode(400)
        .end("Invalid request body: " + e.getMessage());
    }
  }

  private void updateActivity(RoutingContext rc) {
    String id = rc.pathParam("id");
    try {
      io.vertx.core.json.JsonObject body = rc.getBodyAsJson();
      Activity activity = new Activity();
      activity.setTitle(body.getString("title"));
      activity.setDescription(body.getString("description"));
      
      String dateTimeStr = body.getString("dateTime");
      if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
        activity.setDateTime(LocalDateTime.parse(dateTimeStr, ISO_FORMATTER));
      }
      
      activity.setNotificationEnabled(body.getBoolean("notificationEnabled", false));

      activityService.updateActivity(id, activity)
        .onSuccess(updated -> {
          String json = convertActivityToJson(updated);
          rc.response()
            .putHeader("content-type", "application/json")
            .end(json);
        })
        .onFailure(err -> {
          rc.response()
            .setStatusCode(404)
            .end("Error: " + err.getMessage());
        });
    } catch (Exception e) {
      rc.response()
        .setStatusCode(400)
        .end("Invalid request body: " + e.getMessage());
    }
  }

  private void deleteActivity(RoutingContext rc) {
    String id = rc.pathParam("id");
    activityService.deleteActivity(id)
      .onSuccess(deleted -> {
        if (deleted) {
          rc.response().setStatusCode(204).end();
        } else {
          rc.response().setStatusCode(404).end("Activity not found");
        }
      })
      .onFailure(err -> {
        rc.response()
          .setStatusCode(500)
          .end("Error: " + err.getMessage());
      });
  }

  private String convertActivityToJson(Activity activity) {
    if (activity == null) return "{}";
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("\"id\":\"").append(escapeJson(activity.getId())).append("\",");
    sb.append("\"title\":\"").append(escapeJson(activity.getTitle())).append("\",");
    sb.append("\"description\":\"").append(escapeJson(activity.getDescription())).append("\",");
    sb.append("\"dateTime\":");
    if (activity.getDateTime() != null) {
      sb.append("\"").append(activity.getDateTime().format(ISO_FORMATTER)).append("\"");
    } else {
      sb.append("null");
    }
    sb.append(",");
    sb.append("\"notificationEnabled\":").append(activity.isNotificationEnabled()).append(",");
    sb.append("\"notified\":").append(activity.isNotified());
    sb.append("}");
    return sb.toString();
  }

  private String convertActivitiesToJson(List<Activity> activities) {
    if (activities == null || activities.isEmpty()) return "[]";
    return "[" + activities.stream()
      .map(this::convertActivityToJson)
      .collect(Collectors.joining(",")) + "]";
  }

  private String escapeJson(String s) {
    if (s == null) return "";
    return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
  }
}

