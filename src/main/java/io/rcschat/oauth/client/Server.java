package io.rcschat.oauth.client;

import io.rcschat.oauth.storage.CacheProvider;
import io.rcschat.oauth.storage.impl.LocalCache;
import io.rcschat.oauth.storage.impl.RedisStorage;
import io.rcschat.oauth.client.handlers.OauthStartHandler;
import io.rcschat.oauth.client.handlers.OauthGetHandler;
import io.rcschat.oauth.client.handlers.OauthStopHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server extends AbstractVerticle {

    private CacheProvider<String, String> cacheProvider;

    @Override
    public void start() {
        final JsonObject redisConfig = config().getJsonObject("redis");
        if (redisConfig != null) {
            final RedisOptions options = new RedisOptions().setHost(redisConfig.getString("host")).setPort(redisConfig.getInteger("port"));
            if (redisConfig.getString("password") != null) {
                options.setAuth(redisConfig.getString("password"));
            }
            cacheProvider = new RedisStorage(RedisClient.create(vertx, options), RedisClient.create(vertx, options));
        } else {
            cacheProvider = new LocalCache<String, String>(vertx, "oauth");
        }

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        routing(router);

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(8080, handler -> {
                if (handler.succeeded()) {
                    log.info("Listening on port 8080");
                } else {
                    log.error("Failed to listen on port 8080: {}", handler.cause());
                }
            });
    }

    private void routing(@NonNull final Router router) {
        router.post("/oauth")
              .handler(new OauthStartHandler(vertx, cacheProvider));
        router.get("/oauth/:subchannel/:clientId")
              .handler(new OauthGetHandler(vertx, cacheProvider));
        router.delete("/oauth/:subchannel/:clientId")
              .handler(new OauthStopHandler(vertx, cacheProvider));
    }
}