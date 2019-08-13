package cn.edu.gzmu.authorization.web

import cn.edu.gzmu.authorization.model.constant.*
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.kotlin.core.eventbus.requestAwait
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

/**
 * Dispatch Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/13 下午3:47
 */
abstract class DispatchVerticle : CoroutineVerticle() {

  abstract suspend fun getVerticleAddressByPath(path: String): String

  override suspend fun start() {
    val router = Router.router(vertx)

    // -----------------------------------------------------------------------------------------------------------------
    router.route()
      .handler(CookieHandler.create())
      .handler(
        CorsHandler.create(".*")
          .allowCredentials(true)
          .allowedMethods(setOf(HttpMethod.OPTIONS, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))
          .allowedHeader("*")
          .maxAgeSeconds(3600)
      )
      .handler(BodyHandler.create().setBodyLimit(1 * 1048576L)) //1MB = 1048576L
      .handler { routingContext ->
        routingContext.response().isChunked = true
        routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
        routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*")
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        routingContext.next()
      }
    // -----------------------------------------------------------------------------------------------------------------
    val routingHandler = { routingContext: RoutingContext ->

      val requestJson = jsonObjectOf()
      val path = routingContext.request().path()
      val httpMethod = routingContext.request().method()
      val cookies = routingContext.cookies()
      val headers = routingContext.request().headers()
      val params = routingContext.queryParams()
      val attributes = routingContext.request().formAttributes()

      requestJson.put(PATH, path)

      // --------------------------- method
      requestJson.put(HTTP_METHOD, httpMethod)

      // --------------------------- cookies
      var json = jsonObjectOf()
      for (cookie in cookies) {
        json.put(cookie.name, cookie.value)
      }
      requestJson.put(COOKIES, json)

      // --------------------------- headers
      json = jsonObjectOf()
      for (header in headers) {
        json.put(header.key, header.value)
      }
      requestJson.put(HEADERS, json)

      // --------------------------- query param
      json = jsonObjectOf()
      var iterator = params.iterator()
      while (iterator.hasNext()) {
        val i = iterator.next()
        json.put(i.key, i.value)
      }
      requestJson.put(QUERY_PARAM, json)

      // --------------------------- form attributes
      json = jsonObjectOf()
      iterator = attributes.iterator()
      while (iterator.hasNext()) {
        val i = iterator.next()
        if (json.containsKey(i.key)) {
          var index = 0
          while (json.containsKey("${i.key}$index")) {
            index++
          }
          json.put("${i.key}$index", i.value)
        } else
          json.put(i.key, i.value)
      }
      requestJson.put(FORM_ATTRIBUTES, json)

      val contentType = headers.get(HttpHeaders.CONTENT_TYPE)
      // --------------------------- body
      if (contentType != null && contentType.contentEquals(HttpHeaderValues.APPLICATION_JSON)) {
        requestJson.put(BODY, routingContext.bodyAsJson)
      }

      json = jsonObjectOf()
      val json2 = jsonObjectOf()
      for (f in routingContext.fileUploads()) {
        json.put(f.name(), f.uploadedFileName())
        json2.put(f.name(), f.fileName())
      }
      requestJson.put(UPLOAD_FILES, json)
      requestJson.put(UPLOAD_FILE_NAMES, json2)

      // --------------------------- launch
      launch {
        val address = getVerticleAddressByPath(path)
        val responseJson = if (address != "") {
          vertx.eventBus().requestAwait<JsonObject>(address, requestJson).body()
        } else {
          JsonObject()
        }
        // --------------------------- response
        when {
          responseJson.containsKey(RESPONSE_JSON) -> routingContext.response().end(
            responseJson.getJsonObject(
              RESPONSE_JSON
            ).toBuffer()
          )
          responseJson.containsKey(EMPTY_RESPONSE) -> routingContext.response().end()
          else -> {
            routingContext.response().statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            routingContext.response().statusMessage = HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()
            routingContext.response().end()
          }
        }
      }
      Unit
    }

    // --------------------------- handle
    router.get("/*").handler(routingHandler)
    router.post("/*").handler(routingHandler)
    router.put("/*").handler(routingHandler)

    // --------------------------- start
    val httpServer = vertx.createHttpServer()
    httpServer.requestHandler(router).listenAwait(8888)
  }


}
