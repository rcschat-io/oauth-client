package io.rcschat.oauth.client;

import io.rcschat.oauth.client.utils.DeployUtils;

import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Deployer {
    private static final VertxOptions vertxOptions = new VertxOptions();

    public static void main(String[] args) {
        final JsonObject config = DeployUtils.getConfig("config.json");
        final Vertx vertx = Vertx.vertx(vertxOptions);
        final DeploymentOptions deployOptions = new DeploymentOptions().setConfig(config);
        deploy(vertx, deployOptions);
    }

    private static void deploy(@NonNull final Vertx vertx, @NonNull final DeploymentOptions deployOptions) {
        deployOptions.setInstances(vertxOptions.getEventLoopPoolSize());

        final List<Future> futures = new ArrayList<>();
        final Promise<Void> serverDeployPromise = Promise.promise();
        futures.add(serverDeployPromise.future());
        DeployUtils.deployService(vertx, Server.class.getName(), deployOptions, serverDeployPromise);

        CompositeFuture.all(futures).setHandler(ar -> {
            if (ar.failed()) {
                log.error("Service deployment failed! Server is not set up!");
            }
        });
    }
}
