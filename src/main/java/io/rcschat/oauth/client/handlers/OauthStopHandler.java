package io.rcschat.oauth.client.handlers;

import io.rcschat.oauth.storage.CacheProvider;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;

@Slf4j
public class OauthStopHandler extends OauthHandler {

    public OauthStopHandler(final Vertx vertx, final CacheProvider<String, String> storage) {
        super(vertx, storage);
    }

    @Override
    public void handle(final RoutingContext ctx) {
        final String clientId = ctx.request().getParam("clientId");
        final String subchannel = ctx.request().getParam("subchannel");
        log.info("handle(): stop token refresh for clientId[{}] on subchannel[{}]", clientId, subchannel);

        oauthHandler.getClientCredential(clientId, subchannel).setHandler(ar -> {
            if (ar.failed()) {
                log.warn("handle(): no ClientCredential to delete for clientId[{}] on subchannel[{}], cause: {}",
                    clientId, subchannel, ar.cause());
                sendResponse(ctx, HttpURLConnection.HTTP_OK);
            } else {
                oauthHandler.deleteClientCredential(ar.result()).setHandler(tr -> {
                    if (tr.succeeded()) {
                        sendResponse(ctx, HttpURLConnection.HTTP_OK);
                    } else {
                        log.error("handle(): fail to revoke token or delete ClientCredential for clientId[{}] on subchannel[{}], cause: {}",
                            clientId, subchannel, tr.cause());
                        sendResponse(ctx, HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                });
            }
        });
    }
}
