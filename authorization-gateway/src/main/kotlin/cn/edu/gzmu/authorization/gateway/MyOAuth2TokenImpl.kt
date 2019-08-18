package cn.edu.gzmu.authorization.gateway

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.AccessToken
import io.vertx.ext.auth.oauth2.impl.OAuth2API
import io.vertx.ext.auth.oauth2.impl.OAuth2TokenImpl

import java.io.UnsupportedEncodingException
import java.util.Base64

import io.vertx.ext.auth.oauth2.impl.OAuth2API.*
import java.util.stream.Collectors

/**
 * @author [EchoCow](https://echocow.cn)
 * @date 2019/8/18 下午3:18
 */
class MyOAuth2TokenImpl(authProvider: MyOAuth2AuthProviderImpl, authInfo: JsonObject) :
  OAuth2TokenImpl(authProvider, authInfo) {

  override fun introspect(tokenType: String, handler: Handler<AsyncResult<Void>>): AccessToken {
    val headers = JsonObject()
    val provider = provider
    val config = provider.config

    if (config.isUseBasicAuthorizationHeader) {
      val basic = config.clientID + ":" + if (config.clientSecret == null) "" else config.clientSecret
      headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(basic.toByteArray()))
    }

    val tmp = config.headers
    if (tmp != null) {
      headers.mergeIn(tmp)
    }

    val form = JsonObject()
      .put("token", principal().getString(tokenType))
      // optional param from RFC7662
      .put("token_type_hint", tokenType)

    headers.put("Content-Type", "application/x-www-form-urlencoded")
    val payload = Buffer.buffer(stringify(form))
    // specify preferred accepted accessToken type
    headers.put("Accept", "application/json,application/x-www-form-urlencoded;q=0.9")

    OAuth2API.fetch(
      provider.vertx,
      config,
      HttpMethod.POST,
      config.introspectionPath,
      headers,
      payload
    ) { res ->
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()))
        return@fetch
      }

      val reply = res.result()

      if (reply.body() == null || reply.body().length() == 0) {
        handler.handle(Future.failedFuture("No Body"))
        return@fetch

      }

      val json: JsonObject

      if (reply.`is`("application/json")) {
        try {
          json = reply.jsonObject()
        } catch (e: RuntimeException) {
          handler.handle(Future.failedFuture(e))
          return@fetch

        }

      } else if (reply.`is`("application/x-www-form-urlencoded") || reply.`is`("text/plain")) {
        try {
          json = queryToJSON(reply.body().toString())
        } catch (e: UnsupportedEncodingException) {
          handler.handle(Future.failedFuture(e))
          return@fetch

        } catch (e: RuntimeException) {
          handler.handle(Future.failedFuture(e))
          return@fetch

        }

      } else {
        handler.handle(Future.failedFuture("Cannot handle accessToken type: " + reply.headers().get("Content-Type")))
        return@fetch

      }

      try {
        if (json.containsKey("error")) {
          var description: String
          val error = json.getValue("error")
          if (error is JsonObject) {
            description = error.getString("message")
          } else {
            // attempt to handle the error as a string
            try {
              description = json.getString("error_description", json.getString("error"))
            } catch (e: RuntimeException) {
              description = error.toString()
            }

          }
          handler.handle(Future.failedFuture(description))
        } else {
          // RFC7662 dictates that there is a boolean active field (however tokeninfo implementations do not return this)
          if (json.containsKey("active") && !json.getBoolean("active", false)) {
            handler.handle(Future.failedFuture("Inactive Token"))
            return@fetch
          }
          // OPTIONALS

          if (json.containsKey("scope") && json.getValue("scope") != null) {
            // A JSON string containing a space-separated list of scopes associated with this token
            val scopes = json.getJsonArray("scope").stream().map {
              it.toString()
            }.collect(Collectors.joining(" "))
            principal().put("scope", scopes)
          }

          // validate client id
          if (json.containsKey("client_id")) {
            if (principal().containsKey("client_id")) {
              if (json.getString("client_id", "") != principal().getString("client_id")) {
                // Client identifier for the OAuth 2.0 client that requested this token.
                handler.handle(Future.failedFuture("Wrong client_id"))
                return@fetch
              }
            } else {
              principal().put("client_id", json.getString("client_id"))

            }
          }

          if (json.containsKey("username")) {
            // Human-readable identifier for the resource owner who authorized this token.
            principal().put("username", json.getString("username"))
          }

          // validate token type
          if (json.containsKey("token_type")) {
            if (principal().containsKey("token_type")) {
              if (!json.getString("token_type", "").equals(
                  principal().getString("token_type"),
                  ignoreCase = true
                )
              ) {
                // Client identifier for the OAuth 2.0 client that requested this token.
                handler.handle(Future.failedFuture("Wrong token_type"))
                return@fetch
              }
            } else {
              principal().put("token_type", json.getString("token_type"))
            }
          }

          try {
            processNonStandardHeaders(json, reply, config.scopeSeparator)
            // reset the access token

            if (json.containsKey("expires_in")) {
              // reset the expires in value and reset the pre calculated value
              principal()
                .put("expires_in", json.getValue("expires_in"))
                .remove("expires_at")
            }

            // All dates in JWT are of type NumericDate
            // a NumericDate is: numeric value representing the number of seconds from 1970-01-01T00:00:00Z UTC until
            // the specified UTC date/time, ignoring leap seconds
            val now = System.currentTimeMillis() / 1000

            if (json.containsKey("iat")) {
              val iat = json.getLong("iat")
              // issue at must be in the past
              if (iat > now + config.jwtOptions.leeway) {
                handler.handle(Future.failedFuture("Invalid token: iat > now"))
                return@fetch
              }
            }

            if (json.containsKey("exp")) {
              val exp = json.getLong("exp")

              if (now - config.jwtOptions.leeway >= exp) {
                handler.handle(Future.failedFuture("Invalid token: exp <= now"))
                return@fetch
              }

              // reset the expires in value and reset the pre calculated value
              principal()
                .put("expires_in", exp!! - now)
                .remove("expires_at")
            }

            // force a init
            init(principal())

            handler.handle(Future.succeededFuture())
          } catch (e: RuntimeException) {
            handler.handle(Future.failedFuture(e))
          }

        }
      } catch (e: RuntimeException) {
        handler.handle(Future.failedFuture(e))
      }
    }

    return this
  }


}
