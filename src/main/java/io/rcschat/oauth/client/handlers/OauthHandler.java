package io.rcschat.oauth.client.handlers;

import io.rcschat.oauth.storage.CacheProvider;
import io.rcschat.oauth.handlers.OAuthHandler;

import io.vertx.core.Vertx;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpClientOptions;

import java.net.HttpURLConnection;

public abstract class OauthHandler implements Handler<RoutingContext> {
    protected OAuthHandler oauthHandler;

    public OauthHandler(final Vertx vertx, final CacheProvider<String, String> storage) {
        final HttpClientOptions clientoptions = new HttpClientOptions();
        clientoptions.setKeepAlive(true);
        oauthHandler = new OAuthHandler(vertx, storage, vertx.createHttpClient(), vertx.createHttpClient(clientoptions.setSsl(true)));
    }

    @Override
    public void handle(final RoutingContext ctx) {
        sendResponse(ctx, HttpURLConnection.HTTP_NOT_FOUND);
    }

    protected void sendResponse(final RoutingContext ctx, int statusCode) {
        ctx.response()
            .setStatusCode(statusCode)
            .end();
    }

    protected void sendResponseWithBody(final RoutingContext ctx, int statusCode, String body) {
        ctx.response()
            .setStatusCode(statusCode)
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .end(body);
    }
}