package io.rcschat.oauth.client.handlers;

import io.rcschat.oauth.storage.CacheProvider;
import io.rcschat.oauth.utils.ServiceUtils;
import io.rcschat.oauth.credential.ClientCredential;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.Json;

import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;

@Slf4j
public class OauthStartHandler extends OauthHandler {

    public OauthStartHandler(final Vertx vertx, final CacheProvider<String, String> storage) {
        super(vertx, storage);
    }

    @Override
    public void handle(final RoutingContext ctx) {
        final String request = ctx.getBodyAsString();
        log.debug("handle(): get request: {}", request);

        final ClientCredential clientCredential = ServiceUtils.parseJsonString(request, ClientCredential.class);
        if (clientCredential == null) {
            log.error("handle(): missing ClientCredential in request: {}", request);
            sendResponse(ctx, HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        log.info("handle(): saving {}", clientCredential);

        oauthHandler.saveClientCredential(clientCredential).setHandler(ar -> {
            if (ar.failed()) {
                log.error("handle(): fail to save ClientCredential", ar.getClass());
                sendResponse(ctx, HttpURLConnection.HTTP_INTERNAL_ERROR);
            } else {
                oauthHandler.requestToken(clientCredential).setHandler(tr -> {
                    if (tr.succeeded()) {
                        sendResponseWithBody(ctx, HttpURLConnection.HTTP_OK, Json.encode(tr.result()));
                    } else {
                        log.error("handle(): fail to requestToken for clientId[{}] on subchannel[{}], cause: {}",
                            clientCredential.getClientId(), clientCredential.getSubchannel(), ar.cause());
                        sendResponse(ctx, HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                });
            }
        });
    }
}
