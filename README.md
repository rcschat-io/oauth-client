# OAuth2 Client

This repository contains examples to show how to use the [OAuth2 SDK](https://apidoc.rcschat.io/apidoc/oauthsdk/index.html?overview-summary.html) to maintain valid access token following the [OAuth 2.0 Authorization Framework](https://tools.ietf.org/html/rfc6749). You may choose to integrate the [oauth-sdk-1.0.0-all.jar](https://rcschat.io/sdk/oauth-sdk/oauth-sdk-1.0.0-all.jar) with your microservice as the example demonstrated.

Alternatively, the OAuth2 client in the example can be started as an independent microservice providing valid tokens through RESTful API.

## Running the client
### Gradle users
1. Build the jar at the command line
    ```
    ./gradlew shadowJar
    ```
2. (Optional) To share tokens between multiple servers, create [Redis](https://redis.io/) config.json in config folder.
    ```
    {
        "redis": {
            "host": "127.0.0.1",
            "port": 6379,
            "password": "password"
        }
    }
    ```
3. Start the client at the command line
    ```
    java -jar oauth-client-1.0.0-all.jar
    ```

## Calling the RESTful API
1. First, save the [ClientCredential](https://apidoc.rcschat.io/apidoc/oauthsdk/io/rcschat/oauth/credential/ClientCredential.html).
    ```
    curl -v -X POST http://localhost:8080/oauth -d '{"clientId": "demo", "clientSecret": "XxMx9mWO6XST", "channel": "rcschat", "subchannel": "rcschat-prod", "scope": "botmessage,content", "tokenUrl": "https://api.rcschat.io/oauth2/v1/token", "revokeTokenUrl": "https://api.rcschat.io/oauth2/v1/revoke"}'
    ```

2. The client will refresh the token in the background, a valid token can be queried as
    ```
    curl -v -X GET http://localhost:8080/oauth/rcschat-prod/demo
    ```

3. Stop the token refresh
    ```
    curl -v -X DELETE http://localhost:8080/oauth/rcschat-prod/demo
    ```