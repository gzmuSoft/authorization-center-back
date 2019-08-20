package cn.edu.gzmu.authorization.gateway

import cn.edu.gzmu.authorization.common.ok
import cn.edu.gzmu.authorization.common.unauthorized
import io.netty.handler.codec.http.HttpHeaderNames
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/18 下午5:30
 */
class Oauth2Handler(vertx: Vertx) {

  val credentials = JsonObject()
    .put("clientID", "authorization-center")
    .put("clientSecret", "secret")
    .put("site", "http://118.24.1.170:8888")

  private val oauth2 = {
    val oAuth2ClientOptions = OAuth2ClientOptions()
    oAuth2ClientOptions.userInfoPath = "/oauth/check_token"
    oAuth2ClientOptions.introspectionPath = "/oauth/check_token"
    oAuth2ClientOptions.clientID = "authorization-center"
    oAuth2ClientOptions.clientSecret = "secret"
    oAuth2ClientOptions.site = "http://118.24.1.170:8888"
    MyOAuth2AuthProviderImpl(vertx, oAuth2ClientOptions)
  }

  fun decryptToken(context: RoutingContext) {
    val token = context.request().headers().get(HttpHeaderNames.AUTHORIZATION)
    if (token != null && token.startsWith("Bearer ")) {
      oauth2().authenticate(json {
        obj(
          "token_type" to "Bearer",
          "access_token" to token.substringAfter("Bearer ", token)
        )
      }) {
        if (it.succeeded()) {
          context.setUser(it.result())
          context.next()
        } else unauthorized(context, JsonObject(it.cause().message?.substring(1)))
      }
    } else context.next()
  }

  fun authorize(context: RoutingContext) {
    val authorizeURL = oauth2().authorizeURL(json {
      obj(
        "redirect_uri" to REDIRECT_URI,
        "scope" to "all"
      )
    })
    ok(context, json {
      obj(
        "url" to authorizeURL
      )
    })
  }

  fun login(context: RoutingContext) {
    val code = context.request().getParam("code")
    oauth2().authenticate(JsonObject().put("code", code).put("redirect_uri", REDIRECT_URI)) { res ->
      if (res.failed()) {
        res.cause().printStackTrace()
        unauthorized(context, ex = res.cause())
      } else {
        context.setUser(res.result())
        ok(context, res.result().principal())
      }
    }
  }

}
