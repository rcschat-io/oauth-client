package io.rcschat.oauth.client.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeployUtils {
    public static JsonObject getConfig(final String filename) {
        final Path path = Paths.get(System.getProperty("user.dir"), "config", filename);
        log.debug("Read config file from {}", path);
        JsonObject config = null;
        try {
            config = new JsonObject(new String(Files.readAllBytes(path)));
        } catch (IOException|SecurityException|OutOfMemoryError e) {
            log.warn("Config file reading error: {}", e);
        }
        return config;
    }

    public static void deployService(@NonNull final Vertx vertx, @NonNull final String service,
            @NonNull final DeploymentOptions deployOptions, @NonNull final Promise<Void> promise) {
        vertx.deployVerticle(service, deployOptions, ar -> {
            if (ar.succeeded()) {
                log.info("{} deployment id is: {}", service, ar.result());
                promise.complete();
            } else {
                log.error("{} deployment failed! cause: {}", service, ar.cause());
                promise.fail(ar.result());
            }
        });
    }
}
