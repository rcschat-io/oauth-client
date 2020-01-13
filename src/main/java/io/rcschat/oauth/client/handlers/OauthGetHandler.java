package io.rcschat.oauth.client.handlers;

import io.rcschat.oauth.storage.CacheProvider;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.Json;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;

@Slf4j
public class OauthGetHandler extends OauthHandler {

    public OauthGetHandler(final Vertx vertx, final CacheProvider<String, String> storage) {
        super(vertx, storage);
    }

    @Override
    public void handle(final RoutingContext ctx) {
        final String clientId = ctx.request().getParam("clientId");
        final String subchannel = ctx.request().getParam("subchannel");
        log.info("handle(): get token for clientId[{}] on subchannel[{}]", clientId, subchannel);

        oauthHandler.getAccessToken(clientId, subchannel).setHandler(ar -> {
            if (ar.succeeded()) {
                sendResponseWithBody(ctx, HttpURLConnection.HTTP_OK, Json.encode(ar.result()));
            } else {
                oauthHandler.requestToken(clientId, subchannel).setHandler(tr -> {
                    if (tr.succeeded()) {
                        sendResponseWithBody(ctx, HttpURLConnection.HTTP_OK, Json.encode(tr.result()));
                    } else {
                        log.error("handle(): fail to requestToken for clientId[{}] on subchannel[{}], cause: {}",
                            clientId, subchannel, tr.cause());
                        sendResponse(ctx, HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                });
            }
        });
    }
}
