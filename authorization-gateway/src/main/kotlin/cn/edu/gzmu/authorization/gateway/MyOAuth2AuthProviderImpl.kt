package cn.edu.gzmu.authorization.gateway

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.ext.auth.oauth2.impl.OAuth2AuthProviderImpl

/**
 * @author [EchoCow](https://echocow.cn)
 * @date 2019/8/18 下午3:37
 */
class MyOAuth2AuthProviderImpl(vertx: Vertx, config: OAuth2ClientOptions) : OAuth2AuthProviderImpl(vertx, config) {

  override fun authenticate(authInfo: JsonObject, resultHandler: Handler<AsyncResult<User>>) {
    // if the authInfo object already contains a token validate it to confirm that it
    // can be reused, otherwise, based on the configured flow, request a new token
    // from the authority provider

    if (
    // authInfo contains a token_type of Bearer
      authInfo.containsKey("token_type") && "Bearer".equals(
        authInfo.getString("token_type"),
        ignoreCase = true
      ) &&
      // authInfo contains a non null token
      authInfo.containsKey("access_token") && authInfo.getString("access_token") != null
    ) {

      // this validation can be done in 2 different ways:
      // 1) the token is a JWT and in this case if the provider is OpenId Compliant the token can be verified locally
      // 2) the token is an opaque string and we need to introspect it

      // if the JWT library is working in unsecure mode, local validation is not to be trusted

      val oauth2Token = MyOAuth2TokenImpl(this, authInfo)

      // the token is not a JWT or there are no loaded keys to validate
      if (oauth2Token.accessToken() == null || jwt.isUnsecure) {
        // the token is not in JWT format or this auth provider is not configured for secure JWTs
        // in this case we must rely on token introspection in order to know more about its state
        // attempt to create a token object from the given string representation

        // perform the introspection
        oauth2Token.introspect { introspect ->
          if (introspect.failed()) {
            resultHandler.handle(Future.failedFuture(introspect.cause()))
            return@introspect
          }
          // the access token object should have updated it's claims/authorities plus expiration, recheck
          if (oauth2Token.expired()) {
            resultHandler.handle(Future.failedFuture("Expired token"))
            return@introspect
          }
          // return self
          resultHandler.handle(Future.succeededFuture(oauth2Token))
        }
      } else {
        // a valid JWT token should have the access token value decoded
        // the token might be valid, but expired
        if (oauth2Token.expired()) {
          resultHandler.handle(Future.failedFuture("Expired Token"))
        } else {
          resultHandler.handle(Future.succeededFuture(oauth2Token))
        }
      }

    } else {
      // the authInfo object does not contain a token, so rely on the
      // configured flow to retrieve a token for the user
      flow.getToken(authInfo) { getToken ->
        if (getToken.failed()) {
          resultHandler.handle(Future.failedFuture(getToken.cause()))
        } else {
          resultHandler.handle(Future.succeededFuture(getToken.result()))
        }
      }
    }
  }

}
