package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

  private ActivityService activityService;
  private ActivityRoutes activityRoutes;
  private NotificationScheduler notificationScheduler;

  @Override
  public void start(Promise<Void> startPromise) {
    // Initialize services
    activityService = new ActivityService(vertx);
    activityRoutes = new ActivityRoutes(activityService);

    // Create router
    Router router = Router.router(vertx);

    // Setup API routes
    Router apiRouter = activityRoutes.createRouter();
    router.mountSubRouter("/api", apiRouter);

    // Setup static file serving for web frontend
    router.route("/*").handler(StaticHandler.create("web")
      .setIndexPage("index.html"));

    // Health check endpoint
    router.get("/health").handler(ctx -> {
      ctx.response()
        .putHeader("content-type", "application/json")
        .end(Json.encodePrettily(new JsonObject().put("status", "UP")));
    });

    // Deploy notification scheduler
    notificationScheduler = new NotificationScheduler();
    vertx.deployVerticle(notificationScheduler)
      .onSuccess(deploymentId -> {
        System.out.println("NotificationScheduler deployed with id: " + deploymentId);
        
        // Start HTTP server
        vertx.createHttpServer()
          .requestHandler(router)
          .listen(8888)
          .onSuccess(http -> {
            System.out.println("HTTP server started on port 8888");
            System.out.println("Web interface: http://localhost:8888");
            System.out.println("API endpoints: http://localhost:8888/api/activities");
            startPromise.complete();
          })
          .onFailure(err -> {
            System.err.println("Failed to start HTTP server: " + err.getMessage());
            startPromise.fail(err);
          });
      })
      .onFailure(err -> {
        System.err.println("Failed to deploy NotificationScheduler: " + err.getMessage());
        startPromise.fail(err);
      });
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    System.out.println("MainVerticle stopping...");
    stopPromise.complete();
  }
}

