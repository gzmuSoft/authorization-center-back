package cn.edu.gzmu.authorization.gateway

import cn.edu.gzmu.authorization.common.*
import com.google.common.base.Splitter
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.http.listenAwait
import kotlinx.coroutines.launch
import io.vertx.kotlin.servicediscovery.getRecordsAwait
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.util.*


/**
 * api gateway
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/16 下午6:28
 */
class ApiGatewayVerticle : RestVerticle() {
  private val log = LoggerFactory.getLogger(ApiGatewayVerticle::class.java)

  private val oauth2 = {
    val oAuth2ClientOptions = OAuth2ClientOptions()
    oAuth2ClientOptions.userInfoPath = "/oauth/check_token"
    oAuth2ClientOptions.introspectionPath = "/oauth/check_token"
    oAuth2ClientOptions.clientID = "authorization-center"
    oAuth2ClientOptions.clientSecret = "secret"
    oAuth2ClientOptions.site = "http://118.24.1.170:8888"
    MyOAuth2AuthProviderImpl(vertx, oAuth2ClientOptions)
  }

  override suspend fun start() {
    super.start()
    val host = config.getString(ADDRESS, LOCALHOST)
    val port = config.getInteger(PORT, DEFAULT_PORT)
    val router = Router.router(vertx)
    enableCookie(router)
    router.route().handler(BodyHandler.create())

    val credentials = JsonObject()
      .put("clientID", "authorization-center")
      .put("clientSecret", "secret")
      .put("site", "http://118.24.1.170:8888")

    router.get("/authorize").handler(this::authorize)
    router.get("/login/:code").handler(this::login)
    router.route().handler(this::decryptToken)
    router.route("/api/*").handler(this::dispatchRequests)

    val httpServerOptions = HttpServerOptions()
    vertx.createHttpServer(httpServerOptions)
      .requestHandler(router::accept)
      .listenAwait(port, host)
    log.info("Success start api gateway on $host:$port")
  }

  private fun decryptToken(context: RoutingContext) {
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
        } else {
          unauthorized(context)
        }
      }
    }
  }

  private fun authorize(context: RoutingContext) {
    val authorizeURL = oauth2().authorizeURL(json {
      obj(
        "redirect_uri" to "http://example.com",
        "scope" to "all"
      )
    })
    ok(context, json {
      obj(
        "url" to authorizeURL
      )
    })
  }

  private fun login(context: RoutingContext) {
    val code = context.request().getParam("code")
    oauth2().authenticate(JsonObject().put("code", code).put("redirect_uri", "http://example.com")) { res ->
      if (res.failed()) {
        res.cause().printStackTrace()
        unauthorized(context, JsonObject(res.cause().message?.substring(1)))
      } else {
        context.setUser(res.result())
        ok(context, res.result().principal())
      }
    }
  }

  private fun dispatchRequests(context: RoutingContext) {
    val initialOffset = 5
    if (Objects.isNull(context.user())) {
      unauthorized(context)
      return
    }
    circuitBreaker.execute<String> {
      launch {
        val records = getAllEndpoints()
        val path = context.request().uri()
        if (path.length <= initialOffset) {
          notFound(context)
          it.complete()
          return@launch
        }
        val prefix = Splitter.on("/").omitEmptyStrings().splitToList(path)[1]

        val newPath = path.substring(initialOffset + prefix.length)
        val client = records.stream()
          .filter { it.metadata.getString(API_NAME) != null }
          .filter { it.metadata.getString(API_NAME) == prefix }
          .findAny()
        if (client.isPresent) {
          doDispatch(context, newPath, discovery.getReference(client.get()).getAs(WebClient::class.java), it)
        } else {
          notFound(context)
          it.complete()
        }
      }
    }.setHandler {
      if (it.failed()) badGateway(context)
    }

  }

  private fun doDispatch(context: RoutingContext, path: String, client: WebClient, promise: Promise<String>) {
    client.request(context.request().method(), path).send { result ->
      if (result.succeeded()) {
        val response = result.result()
        if (response.statusCode() >= HttpResponseStatus.INTERNAL_SERVER_ERROR.code()) {
          log.error("Error: ${response.statusCode()} : ${response.bodyAsString()}")
          promise.fail("Error: ${response.statusCode()} : ${response.bodyAsString()}")
        } else {
          context.response().headers().addAll(response.headers())
          ok(context, response.bodyAsJsonObject())
          promise.complete()
        }
        ServiceDiscovery.releaseServiceObject(discovery, client)
      } else {
        log.error("Error: http $path failed!")
        promise.fail("Error: http $path failed!")
      }
    }
  }

  private fun buildHostURI(): String {
    val port = config.getInteger(PORT, DEFAULT_PORT)
    val host = config.getString(ADDRESS, "localhost")
    return "https://$host:$port"
  }

  private suspend fun getAllEndpoints(): List<Record> {
    return discovery.getRecordsAwait { record ->
      record.type == HttpEndpoint.TYPE
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(ApiGatewayVerticle::class.java.name)
}

